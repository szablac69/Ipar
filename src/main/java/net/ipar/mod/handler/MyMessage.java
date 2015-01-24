package net.ipar.mod.handler;

import org.apache.commons.lang3.Validate;

import com.google.common.base.Charsets;

import net.ipar.mod.tileEntity.TileEntityATM;
import net.minecraft.entity.player.EntityPlayerMP;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import static io.netty.buffer.Unpooled.*;

public class MyMessage implements IMessage{

	private String text;
	
	int x, y, z ;
	    
	public MyMessage(){
	}
	
    public MyMessage(String text, TileEntityATM ATM) {
        this.text = text;
        this.x = ATM.xCoord;
        this.y = ATM.yCoord;
        this.z = ATM.zCoord;
    }
      
	@Override
	public void fromBytes(ByteBuf buf) {
		text = ByteBufUtils.readUTF8String(buf);
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, text);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	
	public static class Handler implements IMessageHandler<MyMessage, IMessage> {
	       
        @Override
        public IMessage onMessage(MyMessage message, MessageContext ctx) {
        	//System.out.println(String.format("Received %s from %s", message.text, ctx.getServerHandler().playerEntity.getDisplayName()));
        	
        	EntityPlayerMP playerMP = (EntityPlayerMP)ctx.getServerHandler().playerEntity;
        	TileEntityATM entity = (TileEntityATM)playerMP.worldObj.getTileEntity(message.x,message. y, message.z);
            if(entity != null) entity.sell();

            return null; // no response in this case
        }
    }
}
