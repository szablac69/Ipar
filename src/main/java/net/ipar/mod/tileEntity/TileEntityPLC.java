package net.ipar.mod.tileEntity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.core.appender.FileManager;

import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import cpw.mods.fml.relauncher.Side;
import mrtjp.projectred.api.IBundledEmitter;
import mrtjp.projectred.api.IBundledTile;
import mrtjp.projectred.api.IBundledTileInteraction;
import mrtjp.projectred.api.IConnectable;
import mrtjp.projectred.api.ITransmissionAPI;
import mrtjp.projectred.api.ProjectRedAPI;
import net.ipar.mod.Ipar;
import net.ipar.mod.handler.MessagePLC;
import net.ipar.mod.handler.MyMessage;
import net.ipar.mod.utilsPLC.Address24bit;
import net.ipar.mod.utilsPLC.Address24bit.Groups;
import net.ipar.mod.utilsPLC.Instruction;
import net.ipar.mod.utilsPLC.LdIcons.LdIconBase;
import net.ipar.mod.utilsPLC.LdIcons.LdIconEmpty;
import net.ipar.mod.utilsPLC.LdIcons.LdIconNO;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration.UnicodeInputStreamReader;
import cpw.mods.fml.common.FMLCommonHandler;

public class TileEntityPLC extends TileEntity implements IBundledTile {

	public static final int maxRow = 100;
	public LdIconBase[][] ldIcon = new LdIconBase[12][maxRow];
	
	public List<Instruction> lstIstruction = new ArrayList<Instruction>();
	public boolean isValidProgram = false;
	public byte[] memoryMap = new byte[384];
	public int accu = 0;
	public boolean firstRun = true; 
	public short tickCnt;
	public byte status = 0;	// 0 - stop , 1 - run
	
	String saveFileName; 
	
    public TileEntityPLC(){
		super();		
		for(int i = 0;i<maxRow;i++){
			for(int j = 0;j<12;j++){
				ldIcon[j][i] = new LdIconEmpty(false, j, i);
			}
		}
	}
    public void updateEntity(){
    	
    	if(!this.worldObj.isRemote){
    		byte[] signal = new byte[16];
    		int x = this.xCoord;
    		int z = this.zCoord;
    		switch(this.getBlockMetadata()){
    			case 2: z++;break;
    			case 3: z--;break;
    			case 4: x++;break;
    			case 5: x--;break;
    		}

    		signal = ProjectRedAPI.transmissionAPI.getBundledInput(this.worldObj,this.xCoord,this.yCoord,this.zCoord, (this.getBlockMetadata() ^ 1));

    		if(signal != null){
    			for(short i = 0;i<8;i++) Address24bit.setBit(Groups.X, (short) 0, i, this, signal[i] != 0);
    		}        		        		

    		if(lstIstruction.isEmpty()) status = 0; else status = 1;
        	//RUN program
    		tickCnt++;
    		for(Instruction ins : lstIstruction){
    			ins.executeInstruction(this);
    		}
    		firstRun = false;
        
        	this.worldObj.notifyBlockOfNeighborChange(x, this.yCoord, z, blockType);
    	}
    	if(this.worldObj.isRemote){
    		Ipar.network.sendToServer(new MessagePLC(this,0x40));
    	}

    	//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord + 1);
     	
    	//this.markForUpdate();
    }
    
    public byte getBX(){
    	return Address24bit.getByte(Groups.BX, (short)0, this);
    }
    public byte getBY(){
    	return Address24bit.getByte(Groups.BY, (short)0, this);
    }
    
    
    public  byte[] getStraightSignal(TileEntity t, int side, int r) {
    	if (t instanceof IBundledEmitter)
			return getBundledPartSignal(t, 1);
		else if (t instanceof TileMultipart)
			return getBundledPartSignal(((TileMultipart)t).partMap(side), (r+2)%4);
		return null;
    }
    public static byte[] getBundledPartSignal(Object part, int r) {
		if (part instanceof IBundledEmitter)
			return ((IBundledEmitter)part).getBundledSignal(r);
		return null;
	}
    
    @Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		System.out.println("readFromNBT");
		for(int i = 0; i < maxRow;i++){
			for(int j = 0; j < 12; j++){
				this.ldIcon[j][i] = LdIconBase.load(nbt.getInteger(i + "x" + j), j, i);
				//this.ldIcon[j][i] = LdIconBase.load(0x02000402);
			}
		}
	}
    
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		System.out.println("writeToNBT befor super");
		super.writeToNBT(nbt);
		System.out.println("writeToNBT");
		for(int i = 0; i < maxRow;i++){
			for(int j = 0; j < 12; j++){
				nbt.setInteger(i + "x" + j, this.ldIcon[j][i].getSaveInt());
			}
		}
		
	}
	@Override
	public byte[] getBundledSignal(int dir) {
		byte[] signal = new byte[16];
		for(int i = 8;i<16;i++){
			signal[i] =  (byte) (Address24bit.getBit(Groups.Y, 0, i - 8, this) ? 255 : 0);
		}
		return signal;
	}
	@Override
	public boolean canConnectBundled(int side) {
		// TODO Auto-generated method stub
		int s = 0 ;
		if((side ) == (this.getBlockMetadata() ^ 1)){
			return true;
		}
		return false;
	}
	
	


    
}
