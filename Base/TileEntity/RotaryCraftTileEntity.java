/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.RotaryCraft.Base.TileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Interfaces.TextureFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.RotaryCraft.RotaryCraft;
import Reika.RotaryCraft.Auxiliary.RotaryRenderList;
import Reika.RotaryCraft.Base.RotaryModelBase;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import Reika.RotaryCraft.Registry.MachineRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dan200.computer.api.IPeripheral;

public abstract class RotaryCraftTileEntity extends TileEntityBase implements RenderFetcher, IPeripheral {

	protected RotaryModelBase rmb;
	protected int tickcount = 0;
	/** Rotational speed in radians per render tick. */
	public float phi = 0;

	/** For emp */
	private boolean disabled;

	public int[] paint = {-1, -1, -1};

	protected StepTimer second = new StepTimer(20);

	protected boolean isFlipped = false;

	@Override
	public final boolean canUpdate() {
		return true;
	}

	public int getTick() {
		return tickcount;
	}

	@Override
	public abstract void animateWithTick(World world, int x, int y, int z);

	public abstract MachineRegistry getMachine();

	public final TextureFetcher getRenderer() {
		if (this.getMachine().hasRender())
			return RotaryRenderList.getRenderForMachine(this.getMachine());
		else
			return null;
	}

	public final int getMachineIndex() {
		return this.getMachine().ordinal();
	}

	public final String getName() {
		return this.getMachine().getName();
	}

	public final String getMultiValuedName() {
		if (this.getMachine().isMultiNamed())
			return this.getMachine().getMultiName(this);
		return this.getMachine().getName();
	}

	//public abstract int getMachineIndex();

	@Override
	public final int getTileEntityBlockID() {
		return this.getMachine().getBlockID();
	}

	public void giveNoSuperWarning() {
		ReikaJavaLibrary.pConsole("TileEntity "+this.getName()+" does not call super()!");
		ReikaChatHelper.write("TileEntity "+this.getName()+" does not call super()!");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final double getMaxRenderDistanceSquared() {
		return 4096D;
	}

	@Override
	public final boolean shouldRenderInPass(int pass)
	{
		if (!this.isInWorld())
			return true;
		if (pass == 0)
			return true;
		if (this.getMachine().hasModel() && this instanceof TileEntityIOMachine)
			return true;
		if (pass == 1 && (this.hasModelTransparency() || this.getMachine().renderInPass1()))
			return true;
		return false;
	}

	public abstract boolean hasModelTransparency();

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);
		NBT.setFloat("phi", phi);
		NBT.setInteger("tick", tickcount);

		NBT.setBoolean("emp", disabled);
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);
		phi = NBT.getFloat("phi");
		tickcount = NBT.getInteger("tick");

		disabled = NBT.getBoolean("emp");
	}

	public boolean isSelfBlock() {
		if (worldObj.getBlockId(xCoord, yCoord, zCoord) != this.getTileEntityBlockID())
			return false;
		int meta = this.getMachine().getMachineMetadata();
		return meta == worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
	}

	public abstract int getRedstoneOverride();

	public boolean isPlayerAccessible(EntityPlayer var1) {
		if (ConfigRegistry.LOCKMACHINES.getState() && !var1.getEntityName().equals(placer)) {
			ReikaChatHelper.write("This "+this.getName()+" is locked and can only be used by "+placer+"!");
			return false;
		}
		double dist = ReikaMathLibrary.py3d(xCoord+0.5-var1.posX, yCoord+0.5-var1.posY, zCoord+0.5-var1.posZ);
		return (dist <= 8) && worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this;
	}

	@Override
	public String getTEName() {
		MachineRegistry m = this.getMachine();
		if (m.isMultiNamed())
			return m.getMultiName(this);
		return this.getName();
	}

	public boolean isShutdown() {
		return disabled;
	}

	public void onEMP() {
		disabled = true;
		if (this instanceof TileEntityIOMachine) {
			TileEntityIOMachine io = (TileEntityIOMachine)this;
			io.power = 0;
			io.torque = 0;
			io.omega = 0;
		}
	}

	public int getTextureStateForSide(int s) {
		switch(this.getBlockMetadata()) {
		case 0:
			return s == 4 ? this.getActiveTexture() : 0;
		case 1:
			return s == 5 ? this.getActiveTexture() : 0;
		case 2:
			return s == 2 ? this.getActiveTexture() : 0;
		case 3:
			return s == 3 ? this.getActiveTexture() : 0;
		}
		return 0;
	}

	protected int getActiveTexture() {
		return 0;
	}

	public void setFlipped(boolean set) {
		isFlipped = set;
	}

	public boolean isFlipped() {
		return isFlipped;
	}

	public Icon getIconForSide(ForgeDirection dir) {
		return RotaryCraft.decoblock.getIcon(0, 0);
	}

	public boolean hasIconOverride() {
		return false;
	}

	public boolean matchMachine(IBlockAccess world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		int id2 = this.getTileEntityBlockID();
		int meta2 = this.getMachine().getMachineMetadata();
		return id2 == id && meta2 == meta;
	}

	@Override
	public final int getPacketDelay() {
		return DragonAPICore.isSinglePlayer() ? 1 : Math.min(20, ConfigRegistry.PACKETDELAY.getValue());
	}

	@Override
	public String[] getMethodNames() {
		return new String[]{"getName"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
		return new Object[]{this.getTEName()};
	}

	@Override
	public boolean canAttachToSide(int side) {
		return true;
	}

	@Override
	public void attach(IComputerAccess computer) {
		ReikaJavaLibrary.pConsole(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {

	}

	@Override
	public String getType() {
		return this.getMultiValuedName().replaceAll(" ", "");
	}
}
