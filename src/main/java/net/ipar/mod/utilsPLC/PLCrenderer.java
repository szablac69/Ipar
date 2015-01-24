package net.ipar.mod.utilsPLC;

import org.lwjgl.opengl.GL11;

import net.ipar.mod.Ipar;
import net.ipar.mod.tileEntity.TileEntityPLC;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class PLCrenderer extends TileEntitySpecialRenderer{
	public static final ResourceLocation texture = new ResourceLocation(Ipar.modid + ":" + "textures/blocks/PLC_front2.png");
	
	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x,	double y, double z, float f) {

		this.bindTexture(texture);
		
		TileEntityPLC PLC = (TileEntityPLC) tileEntity;
		
		int side = PLC.getBlockMetadata();
		
		//System.out.println("HOHOHO");
		Tessellator tessellator = Tessellator.instance;
	    GL11.glPushMatrix();
	    GL11.glTranslated(x, y, z); // +1 so that our "drawing" appears 1 block over our block (to get a better view)
	    
	    //Z+
	    //GL11.glRotatef(180, 0, 1, 0);
	    //GL11.glTranslatef(-1, 0, 0);
	    
	    //Z-
	    //GL11.glTranslatef(0, 0, 1);
	    
	    //X-
	    //GL11.glRotatef(90, 0, -1, 0);
	    
	    //X+
	    //GL11.glRotatef(90, 0, 1, 0);
	    //GL11.glTranslatef(-1, 0, 1);
	    
	    
	    switch (side) {
		case 0:
			GL11.glTranslatef(0, 1, 0);
			break;
		case 1:
			GL11.glTranslatef(0, 1, 0);
			break;
		case 2:
			//GL11.glTranslatef(0, 1, 0);
			GL11.glRotatef(180, 0, 1, 0);
			GL11.glTranslatef(-1, 0, 0);
			break;
		case 3:
			//GL11.glTranslatef(0, 1, 0);
			GL11.glTranslatef(0, 0, 1);
			break;
		case 4:
			//GL11.glTranslatef(0, 1, 0);
			GL11.glRotatef(90, 0, -1, 0);
			break;
		case 5:
			//GL11.glTranslatef(0, 1, 0);
			GL11.glRotatef(90, 0, 1, 0);
			GL11.glTranslatef(-1, 0, 1);
			break;
		}
	    
	    float pb = (float) 1/16;
	    float pt = (float) 1/32;
	    
	    tessellator.startDrawingQuads();
	    tessellator.addVertexWithUV(16 * pb, 0, 0, 16 * pt, 16 * pt);
	    tessellator.addVertexWithUV(16 * pb, 16 * pb, 0, 16 * pt, 0);
	    tessellator.addVertexWithUV(0, 16 * pb, 0, 0, 0);
	    tessellator.addVertexWithUV(0, 0, 0, 0, 16 * pt);
	    
	    
	    //Input
	    byte in = PLC.getBX();
	    for(int i = 0; i < 8; i++){
	    	/*float xL = 2, xR = 3;
		    float yT = 14, yB = 13;
	    
		    float uL = 17, uR = 18;
		    float vT = 0, vB = 1;*/
		    
	    	float xL = 2 + ((float)i % 2), xR = xL + 1;
		    float yT = 14 - i, yB = yT - 1;
		    
		    float uL,uR;
		    if((in & (1 << i)) == 0) {uL = 16; uR = 17;}
		    else {uL = 17; uR = 18;}
		    float vT = 0, vB = 1;
	    	
		    tessellator.addVertexWithUV(xR * pb, yB * pb, 0, uR * pt, vB * pt);
		    tessellator.addVertexWithUV(xR * pb, yT * pb, 0, uR * pt, vT * pt);
		    tessellator.addVertexWithUV(xL * pb, yT * pb, 0, uL * pt, vT * pt);
		    tessellator.addVertexWithUV(xL * pb, yB * pb, 0, uL * pt, vB * pt);
	    }
	    //Output
	    byte out = PLC.getBY();
	    for(int i = 0; i < 8; i++){
	    	/*float xL = 2, xR = 3;
		    float yT = 14, yB = 13;
	    
		    float uL = 17, uR = 18;
		    float vT = 0, vB = 1;*/
		    
	    	float xL = 13 - ((float)i % 2), xR = xL + 1;
		    float yT = 14 - i, yB = yT - 1;
	    
		    float uL,uR;
		    if((out & (1 << i)) == 0) {uL = 16; uR = 17;}
		    else {uL = 17; uR = 18;}
		    float vT = 0, vB = 1;
	    	
		    tessellator.addVertexWithUV(xR * pb, yB * pb, 0, uR * pt, vB * pt);
		    tessellator.addVertexWithUV(xR * pb, yT * pb, 0, uR * pt, vT * pt);
		    tessellator.addVertexWithUV(xL * pb, yT * pb, 0, uL * pt, vT * pt);
		    tessellator.addVertexWithUV(xL * pb, yB * pb, 0, uL * pt, vB * pt);
	    }
	    //Status
	    
	    {
	    	float xL = 7, xR = 9;
		    float yT = 13, yB = 11;
	    
		    float uL,uR;
		    if(PLC.status == 0) {uL = 18; uR = 20;}
		    else {uL = 16; uR = 18;}
		    float vT = 1, vB = 3;
		    
	    	
		    tessellator.addVertexWithUV(xR * pb, yB * pb, 0, uR * pt, vB * pt);
		    tessellator.addVertexWithUV(xR * pb, yT * pb, 0, uR * pt, vT * pt);
		    tessellator.addVertexWithUV(xL * pb, yT * pb, 0, uL * pt, vT * pt);
		    tessellator.addVertexWithUV(xL * pb, yB * pb, 0, uL * pt, vB * pt);
	    }
	    
	    
	    /*tessellator.addVertexWithUV(0, 0, 0, 0, 0);
	    tessellator.addVertexWithUV(0, 1, 0, 0, 1);
	    tessellator.addVertexWithUV(1, 1, 0, 1, 1);
	    tessellator.addVertexWithUV(1, 0, 0, 1, 0);*/

	    /*tessellator.addVertexWithUV(0, 0, 0, 0, 0);
	    tessellator.addVertexWithUV(1, 0, 0, 1, 0);
	    tessellator.addVertexWithUV(1, 1, 0, 1, 1);
	    tessellator.addVertexWithUV(0, 1, 0, 0, 1);
		*/
	    tessellator.draw();
	    GL11.glPopMatrix();
		
	}

}
