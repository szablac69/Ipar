package net.ipar.mod.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Level;

import net.ipar.mod.Ipar;
import net.ipar.mod.tileEntity.TileEntityPLC;
import net.ipar.mod.utilsPLC.Instruction;
import net.ipar.mod.utilsPLC.LdIcons.LdIconBase;
import net.ipar.mod.utilsPLC.LdIcons.LdIconEmpty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class MessagePLC implements IMessage{

	public int massageType;
	
	TileEntityPLC PLC;
	
	public MessagePLC(){
	}
	/**
	 * 
	 * @param PLC
	 * @param massageType  - 	binárisan kódolva
	 * 							1 létra
	 * 							2 IL
	 * 							4 memoria
	 * 							Client kéri a programot
	 * 								-0x10  - LD-t kéri
	 * 								-0x20  - IL-t kéri
	 * 								-0x40  - Memoryt kéri			
	 * 
	 */
    public MessagePLC(TileEntityPLC PLC,int massageType) {
    	this.massageType = massageType;
    	this.PLC = PLC;
    }
	
	@Override
	public void fromBytes(ByteBuf buf) {
		PLC = new TileEntityPLC();
		PLC.xCoord = buf.readInt();
		PLC.yCoord = buf.readInt();
		PLC.zCoord = buf.readInt();
		this.massageType = buf.readInt();
		
		//Létra program
		if((this.massageType & 0xFFF1) == 0x0001){
			for(int i = 0;i<TileEntityPLC.maxRow;i++){
				for(int j = 0;j<12;j++){
					PLC.ldIcon[j][i] = LdIconBase.load(buf.readInt(), j, i);
				}
			}
		}	
		//IL program
		if((this.massageType & 0xFFF2) == 0x0002){
			int t = buf.readInt();
			for(int i = 0;i < t; i++){
				PLC.lstIstruction.add(new Instruction(buf.readInt(),buf.readShort()));
			}
		}
		//Memoria
		if((this.massageType & 0xFFF4) == 0x0004){
			buf.readBytes(PLC.memoryMap);
			PLC.status = buf.readByte();
		}		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(PLC.xCoord);
		buf.writeInt(PLC.yCoord);
		buf.writeInt(PLC.zCoord);
		buf.writeInt(this.massageType);
		
		//Létra program
		if((this.massageType & 0xFFF1) == 0x0001){
			for(int i = 0;i<TileEntityPLC.maxRow;i++){
				for(int j = 0;j<12;j++){
					buf.writeInt(PLC.ldIcon[j][i].getSaveInt());
				}
			}
		}
		
		
		//IL program
		if((this.massageType & 0xFFF2) == 0x0002){
			buf.writeInt(PLC.lstIstruction.size());
			for(int i = 0; i < PLC.lstIstruction.size(); i++){
				buf.writeInt(PLC.lstIstruction.get(i).adr.address());
				buf.writeShort(PLC.lstIstruction.get(i).cmd.value());
			}
		}
		
		
		//Memoria
		if((this.massageType & 0xFFF4) == 0x0004){
			buf.writeBytes(PLC.memoryMap);
			buf.writeByte(PLC.status);
		}
		
		
	}

	public static class Handler implements IMessageHandler<MessagePLC, IMessage> {
	       
        @Override
        public IMessage onMessage(MessagePLC message, MessageContext ctx) {
            
        	Side side = FMLCommonHandler.instance().getEffectiveSide();
        	if (side == Side.SERVER){
        		EntityPlayerMP playerMP = (EntityPlayerMP)ctx.getServerHandler().playerEntity;
            	//System.out.println(String.format("Server: Type %d from %s", message.massageType, playerMP.getDisplayName()));

        		
        		TileEntityPLC entity = (TileEntityPLC)playerMP.worldObj.getTileEntity(message.PLC.xCoord,message.PLC.yCoord, message.PLC.zCoord);
        		if(entity != null) {
	        		if((message.massageType & 0xFFF1) == 0x0001) {
	        			entity.ldIcon = message.PLC.ldIcon;
	        			entity.markDirty();
	        		}
	            	if((message.massageType & 0xFFF2) == 0x0002) {
	            		entity.lstIstruction = message.PLC.lstIstruction;
	            		entity.firstRun = true;
	            	}
	            	if((message.massageType & 0xFFF4) == 0x0004) entity.memoryMap = message.PLC.memoryMap.clone();
	            	if(message.massageType == 0x10) Ipar.network.sendTo(new MessagePLC(entity,1), playerMP);
	            	if(message.massageType == 0x20) Ipar.network.sendTo(new MessagePLC(entity,2), playerMP);
	            	if(message.massageType == 0x40) Ipar.network.sendTo(new MessagePLC(entity,4), playerMP);
        		}
            	//System.out.println("MessagePLC write elvileg done");

            	
        	}else if(side == Side.CLIENT){
        		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        		//System.out.println(String.format("Client: Type %d from %s", message.massageType, player.getDisplayName()));
        		
        		TileEntityPLC entity = (TileEntityPLC)player.worldObj.getTileEntity(message.PLC.xCoord,message.PLC.yCoord, message.PLC.zCoord);
        		if(entity != null) {
	        		if((message.massageType & 0xFFF1) == 0x0001) {
	        			for(int i = 0;i<TileEntityPLC.maxRow;i++){
	        				for(int j = 0;j<12;j++){
	        					entity.ldIcon[j][i] = LdIconBase.load(message.PLC.ldIcon[j][i].getSaveInt(), j, i);
	        					//entity.ldItem[j][i].setFullCmd(buf.readChar());
	        				}
	        			}
	        			//entity.ldItem = message.PLC.ldItem.clone();
	        		}
	            	if((message.massageType & 0xFFF2) == 0x0002) entity.lstIstruction = message.PLC.lstIstruction;
	            	if((message.massageType & 0xFFF4) == 0x0004) {
	            		entity.memoryMap = message.PLC.memoryMap.clone();
	            		entity.status = message.PLC.status;
	            	}
        		}
            	//System.out.println("MessagePLC write elvileg done");
                
        		
        	}
            
            return null; // no response in this case
        }
        
        
    }
}
