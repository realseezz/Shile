package com.realseezz.shile.client.renderer;

import java.util.Optional;
import java.util.OptionalDouble;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

import com.realseezz.shile.client.ShileClientState;
import com.realseezz.shile.common.ShileConstants;

/**
 * Shile'ı oyuncunun çevresinde, oyuncuya dönük düz bir dörtgen (billboard) olarak çizer.
 * Şimdilik düz siyah yer tutucu; doku (PNG) sonraki adımda eklenecek.
 *
 * Yapı, Fabric 26.2 "Rendering in the World" dokümanındaki özel render pipeline örneğine dayanır.
 * Derinlik testi açık bırakıldı: Shile duvarların arkasında görünmez.
 */
public final class ShileRenderer {
	private static final RenderPipeline SHILE_QUAD = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(ShileConstants.id("pipeline/shile_quad"))
			.build()
	);

	private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
	private static final Vector3f MODEL_OFFSET = new Vector3f();
	private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
	private static final StagedVertexBuffer STAGED_BUFFER =
		new StagedVertexBuffer(() -> "Shile Buffer", RenderType.SMALL_BUFFER_SIZE);

	/** Çıkarım (extraction) aşamasında doldurulur, çizim aşamasında okunur. */
	private static ShileRenderState renderState;

	private ShileRenderer() {
	}

	public static void init() {
		LevelExtractionEvents.END_EXTRACTION.register(ShileRenderer::extract);
		LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(ShileRenderer::renderAndDraw);
		// Doküman GameRenderer#close için mixin öneriyor; mixin gerektirmemesi için kapanışta temizlenir.
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> STAGED_BUFFER.close());
	}

	// --- Çıkarım aşaması ---

	private static void extract(LevelExtractionContext context) {
		Minecraft client = Minecraft.getInstance();
		if (!ShileClientState.isActive() || client.player == null) {
			renderState = null;
			return;
		}
		renderState = new ShileRenderState(ShileClientState.renderYawDeg(), client.player.getEyeHeight());
	}

	// --- Çizim aşaması ---

	private static void renderAndDraw(LevelRenderContext context) {
		ShileRenderState state = renderState;
		if (state == null) {
			return;
		}

		VertexFormat formatBinding = SHILE_QUAD.getVertexFormatBinding(0);
		assert formatBinding != null;

		PrimitiveTopology primitive = SHILE_QUAD.getPrimitiveTopology();
		StagedVertexBuffer.Draw draw = STAGED_BUFFER.appendDraw(formatBinding, primitive,
			primitive == PrimitiveTopology.QUADS ? RenderSystem.getProjectionType().vertexSorting() : null);

		buildQuad(context, draw, state);

		STAGED_BUFFER.upload();

		StagedVertexBuffer.ExecuteInfo info = STAGED_BUFFER.getExecuteInfo(draw);
		if (info != null) {
			draw(Minecraft.getInstance(), info, SHILE_QUAD);
		}

		STAGED_BUFFER.endFrame();
	}

	private static void buildQuad(LevelRenderContext context, StagedVertexBuffer.Draw draw, ShileRenderState state) {
		PoseStack matrices = context.poseStack();
		Vec3 camera = context.levelState().cameraRenderState.pos;

		// Shile'ın konumu: kameranın çevresinde, orbitYawDeg yönünde 2 blok uzakta.
		double theta = Math.toRadians(state.orbitYawDeg());
		double radius = ShileConstants.ORBIT_RADIUS_BLOCKS;
		double centerX = camera.x - Math.sin(theta) * radius;
		double centerZ = camera.z + Math.cos(theta) * radius;

		// Dörtgenin yatay ekseni, kameraya bakan yöne dik.
		double halfWidth = ShileConstants.SHILE_WIDTH_BLOCKS / 2.0;
		double rightX = Math.cos(theta) * halfWidth;
		double rightZ = Math.sin(theta) * halfWidth;

		double bottomY = camera.y - state.eyeHeight();
		double topY = bottomY + ShileConstants.SHILE_HEIGHT_BLOCKS;

		float ax = (float) (centerX - rightX);
		float az = (float) (centerZ - rightZ);
		float bx = (float) (centerX + rightX);
		float bz = (float) (centerZ + rightZ);
		float y0 = (float) bottomY;
		float y1 = (float) topY;

		matrices.pushPose();
		matrices.translate(-camera.x, -camera.y, -camera.z);

		Matrix4fc pose = matrices.last().pose();
		VertexConsumer builder = STAGED_BUFFER.getVertexBuilder(draw);

		// Düz siyah. İki yüz de çizilir, böylece sargı yönü (culling) fark etmez.
		builder.addVertex(pose, ax, y0, az).setColor(0f, 0f, 0f, 1f);
		builder.addVertex(pose, bx, y0, bz).setColor(0f, 0f, 0f, 1f);
		builder.addVertex(pose, bx, y1, bz).setColor(0f, 0f, 0f, 1f);
		builder.addVertex(pose, ax, y1, az).setColor(0f, 0f, 0f, 1f);

		builder.addVertex(pose, ax, y1, az).setColor(0f, 0f, 0f, 1f);
		builder.addVertex(pose, bx, y1, bz).setColor(0f, 0f, 0f, 1f);
		builder.addVertex(pose, bx, y0, bz).setColor(0f, 0f, 0f, 1f);
		builder.addVertex(pose, ax, y0, az).setColor(0f, 0f, 0f, 1f);

		matrices.popPose();
	}

	private static void draw(Minecraft client, StagedVertexBuffer.ExecuteInfo info, RenderPipeline pipeline) {
		GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
			.writeTransform(RenderSystem.getModelViewMatrixCopy(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);

		RenderTarget mainTarget = client.gameRenderer.mainRenderTarget();
		GpuTextureView colorTexture = mainTarget.getColorTextureView();
		assert colorTexture != null;

		try (RenderPass renderPass = RenderSystem.getDevice()
			.createCommandEncoder()
			.createRenderPass(() -> ShileConstants.MOD_ID + " shile render", colorTexture, Optional.empty(),
				mainTarget.getDepthTextureView(), OptionalDouble.empty())) {
			renderPass.setPipeline(pipeline);

			RenderSystem.bindDefaultUniforms(renderPass);
			renderPass.setUniform("DynamicTransforms", dynamicTransforms);

			renderPass.setVertexBuffer(0, info.vertexBuffer().slice());
			renderPass.setIndexBuffer(info.indexBuffer(), info.indexType());
			renderPass.drawIndexed(info.indexCount(), 1, info.firstIndex(), info.baseVertex(), 0);
		}
	}

	/** Değişmez, hızlı oluşturulan çizim durumu (doküman önerisi). */
	private record ShileRenderState(float orbitYawDeg, float eyeHeight) {
	}
}
