package owmii.powah.client.handler;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import owmii.powah.Powah;
import owmii.powah.item.ReactorItem;
import owmii.powah.lib.client.util.Render;
import owmii.powah.lib.client.util.RenderTypes;

import java.util.List;
import java.util.stream.Collectors;

public class ReactorOverlayHandler {
	static final ResourceLocation OV_TEXTURE = new ResourceLocation(Powah.MOD_ID, "textures/misc/reactor_ov.png");

	public static void renderPlacementHighlight(PoseStack matrix, MultiBufferSource bufferSource, BlockHitResult br, Camera camera) {
		Minecraft mc = Minecraft.getInstance();
		net.minecraft.world.entity.player.Player player = mc.player;
		if (player == null || mc.level == null) return;
		boolean flag = false;
		boolean flag1 = false;
		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack stack = player.getItemInHand(hand);
			if (stack.getItem() instanceof ReactorItem) {
				flag = true;
				if (stack.getCount() >= 36) {
					flag1 = true;
					break;
				}
				break;
			}
		}

		if (!flag) return;

		boolean isReplaceable = mc.level.getBlockState(br.getBlockPos()).getMaterial().isReplaceable() && !mc.level.isEmptyBlock(br.getBlockPos());
		if (mc.level.isEmptyBlock(br.getBlockPos()) || !isReplaceable && !br.getDirection().equals(Direction.UP)) return;
		BlockPos pos = isReplaceable ? br.getBlockPos() : br.getBlockPos().relative(br.getDirection());
		List<BlockPos> list = BlockPos.betweenClosedStream(pos.offset(-1, 0, -1), pos.offset(1, 3, 1)).map(BlockPos::immutable).collect(Collectors.toList());
		int color = 0x75e096;
		if (!flag1 && !player.isCreative()) color = 0xcf040e;
		if (color != 0xcf040e) {
			for (BlockPos blockPos : list) {
				if (!mc.level.getBlockState(blockPos).getMaterial().isReplaceable()) {
					color = 0xcf040e;
					break;
				}
			}
			List<LivingEntity> entities = mc.level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(1.0D, 3.0D, 1.0D));
			if (!entities.isEmpty()) {
				color = 0xcf040e;
			}
		}

		matrix.pushPose();

		Vec3 cameraPos = camera.getPosition();
		double x = pos.getX() - cameraPos.x;
		double y = pos.getY() - cameraPos.y;
		double z = pos.getZ() - cameraPos.z;
		matrix.translate(x, y, z);
		matrix.translate(-1.0D, 0.001D, -1.0D);
		float r = (color >> 16 & 0xFF) / 255.0F;
		float g = (color >> 8 & 0xFF) / 255.0F;
		float b = (color & 0xFF) / 255.0F;
		VertexConsumer buffer = bufferSource.getBuffer(RenderTypes.getTextBlended(OV_TEXTURE));
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		buffer.vertex(matrix.last().pose(), 0, 0, 3).color(r, g, b, 1.0F).uv(0.0F, 1.0F).uv2(Render.MAX_LIGHT).endVertex();
		buffer.vertex(matrix.last().pose(), 3, 0, 3).color(r, g, b, 1.0F).uv(1.0F, 1.0F).uv2(Render.MAX_LIGHT).endVertex();
		buffer.vertex(matrix.last().pose(), 3, 0, 0).color(r, g, b, 1.0F).uv(1.0F, 0.0F).uv2(Render.MAX_LIGHT).endVertex();
		buffer.vertex(matrix.last().pose(), 0, 0, 0).color(r, g, b, 1.0F).uv(0.0F, 0.0F).uv2(Render.MAX_LIGHT).endVertex();

		// TODO: is this useful?
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		Minecraft.getInstance().renderBuffers().bufferSource().endLastBatch();

		matrix.popPose();
	}
}
