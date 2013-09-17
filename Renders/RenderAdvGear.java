/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.RotaryCraft.Renders;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.RotaryCraft.Auxiliary.IORenderer;
import Reika.RotaryCraft.Base.MultiModel;
import Reika.RotaryCraft.Base.RotaryCraftTileEntity;
import Reika.RotaryCraft.Base.RotaryTERenderer;
import Reika.RotaryCraft.Base.TileEntityIOMachine;
import Reika.RotaryCraft.Models.ModelCVT;
import Reika.RotaryCraft.Models.ModelCoil;
import Reika.RotaryCraft.Models.ModelWorm;
import Reika.RotaryCraft.TileEntities.Transmission.TileEntityAdvancedGear;

public class RenderAdvGear extends RotaryTERenderer implements MultiModel
{

	private ModelWorm wormModel = new ModelWorm();
	private ModelCVT cvtModel = new ModelCVT();
	private ModelCoil coilModel = new ModelCoil();
	private int itemMetadata = 0;

	/**
	 * Renders the TileEntity for the position.
	 */
	public void renderTileEntityAdvancedGearAt(TileEntityAdvancedGear tile, double par2, double par4, double par6, float par8)
	{
		int var9;

		if (!tile.isInWorld())
		{
			var9 = 0;
		}
		else
		{

			var9 = tile.getBlockMetadata();


			{
				//((BlockAdvGearBlock1)var10).unifyAdjacentChests(tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord);
				var9 = tile.getBlockMetadata();
			}
		}

		if (true)
		{
			ModelWorm var14 = wormModel;
			ModelCVT var15 = cvtModel;
			ModelCoil var16 = coilModel;

			if (true)
			{
				this.bindTextureByName("/Reika/RotaryCraft/Textures/TileEntityTex/shafttex.png");
			}

			GL11.glPushMatrix();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef((float)par2, (float)par4 + 2.0F, (float)par6 + 1.0F);
			GL11.glScalef(1.0F, -1.0F, -1.0F);
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			int var11 = 0;	 //used to rotate the model about metadata

			if (tile.isInWorld()) {

				switch(tile.getBlockMetadata()%4) {
				case 0:
					var11 = 0;
					break;
				case 1:
					var11 = 180;
					break;
				case 2:
					var11 = 90;
					break;
				case 3:
					var11 = 270;
					break;
				}

				GL11.glRotatef((float)var11+180, 0.0F, 1.0F, 0.0F);

			}
			else {
				//ModLoader.getMinecraftInstance().thePlayer.addChatMessage(String.format("%d", this.itemMetadata));
				GL11.glRotatef(-90, 0.0F, 1.0F, 0.0F);
				this.bindTextureByName("/Reika/RotaryCraft/Textures/TileEntityTex/shafttex.png");
				switch(itemMetadata) {
				case 1:
					var14.renderAll(null, 0);
					break;
				case 2:
					this.bindTextureByName("/Reika/RotaryCraft/Textures/TileEntityTex/cvttex.png");
					var15.renderAll(null, 0);
					break;
				case 3:
					this.bindTextureByName("/Reika/RotaryCraft/Textures/TileEntityTex/coiltex.png");
					var16.renderAll(null, 0);
					break;
				}
				if (tile.isInWorld())
					GL11.glDisable(GL12.GL_RESCALE_NORMAL);
				GL11.glPopMatrix();
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				return;
			}

			//GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			//float var12 = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * par8;
			float var13;/*

            var12 = 1.0F - var12;
            var12 = 1.0F - var12 * var12 * var12;*/
			if (tile.getBlockMetadata() < 4)
				var14.renderAll(null, tile.phi);
			else if (tile.getBlockMetadata() < 8) {
				this.bindTextureByName("/Reika/RotaryCraft/Textures/TileEntityTex/cvttex.png");
				var15.renderAll(null, tile.phi);
			}
			else if (tile.getBlockMetadata() < 12) {
				this.bindTextureByName("/Reika/RotaryCraft/Textures/TileEntityTex/coiltex.png");
				var16.renderAll(null, tile.phi);
			}
			if (tile.isInWorld())
				GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		if (par8 <= -999F) {
			itemMetadata = (int)-par8/1000;
			par8 = 0F;
			//ModLoader.getMinecraftInstance().thePlayer.addChatMessage(String.format("%d", this.itemMetadata));
		}
		if (this.isValidMachineRenderpass((RotaryCraftTileEntity)tile))
			this.renderTileEntityAdvancedGearAt((TileEntityAdvancedGear)tile, par2, par4, par6, par8);
		if (((RotaryCraftTileEntity) tile).isInWorld() && MinecraftForgeClient.getRenderPass() == 1)
			IORenderer.renderIO(tile, par2, par4, par6);
	}

	@Override
	public String getImageFileName(RenderFetcher t) {
		if (!(t instanceof RenderFetcher))
			return "";
		TileEntityIOMachine te = (TileEntityIOMachine)t;
		if (te.isInWorld()) {
			if (te.getBlockMetadata() < 4)
				return "shafttex.png";
			else if (te.getBlockMetadata() < 8)
				return "cvttex.png";
			return "coiltex.png";
		}
		else {
			if (itemMetadata == 1)
				return "shafttex.png";
			if (itemMetadata == 3)
				return "coiltex.png";
			return "cvttex.png";
		}
	}
}
