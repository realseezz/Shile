package com.realseezz.shile.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import com.realseezz.shile.common.ShileConstants;
import com.realseezz.shile.common.network.PlayerLookedAtPacket;

/**
 * Bakış algılama (plan 9). Shile'ın herhangi bir parçası oyuncunun görüş alanına (ekranda görünen
 * alan, FOV) girdiği ve arada blok olmadığı anda "bakıyor" sayılır; nişangâhın üstünde olması gerekmez.
 * Kesintisiz 1 saniye (20 tick) sürerse server'a bir kez bildirilir. Görüş alanından çıkarsa ya da
 * blok araya girerse sayaç sıfırlanır.
 */
public final class GazeDetector {
	/** Blok engelini kontrol ederken ışın üzerindeki adım uzunluğu (blok). */
	private static final double OCCLUSION_STEP = 0.25;

	private static int lookTicks;
	/** Bu Shile olayı için bakış zaten bildirildi. */
	private static boolean reported;

	private GazeDetector() {
	}

	public static void reset() {
		lookTicks = 0;
		reported = false;
	}

	/** Her client level tick'inde, Shile hareketi işlendikten sonra çağrılır. */
	public static void tick(LocalPlayer player) {
		if (!ShileClientState.isActive() || reported) {
			return;
		}
		if (isShileInView(player)) {
			lookTicks++;
		} else {
			lookTicks = 0;
		}
		if (lookTicks >= ShileConstants.LOOK_DURATION_TICKS) {
			reported = true;
			ClientPlayNetworking.send(new PlayerLookedAtPacket(lookTicks / (float) ShileConstants.TICKS_PER_SECOND));
		}
	}

	/** Shile dörtgeninin örnek noktalarından en az biri görüş alanında ve engelsiz mi? */
	private static boolean isShileInView(LocalPlayer player) {
		Vec3 eye = player.getEyePosition();

		// Kamera tabanı: ileri, yatay sağ ve yukarı (yaw'dan, böylece tam yukarı/aşağı bakınca bozulmaz).
		Vec3 forward = player.getViewVector(1.0f);
		double yaw = Math.toRadians(player.getYRot());
		Vec3 right = new Vec3(Math.cos(yaw), 0.0, Math.sin(yaw));
		Vec3 up = right.cross(forward);

		// Dikey FOV ayardan okunur (Minecraft'ta ayar dikey FOV'dur); yatay, ekran oranından türetilir.
		double tanVertical = Math.tan(Math.toRadians(Minecraft.getInstance().options.fov().get()) / 2.0);
		double tanHorizontal = tanVertical * ShileConstants.ASSUMED_ASPECT_RATIO;

		// Shile dörtgeninin konumu (renderer ile aynı formül).
		double theta = Math.toRadians(ShileClientState.orbitYawDeg());
		double radius = ShileConstants.ORBIT_RADIUS_BLOCKS;
		double centerX = eye.x - Math.sin(theta) * radius;
		double centerZ = eye.z + Math.cos(theta) * radius;
		double bottomY = eye.y - player.getEyeHeight();
		double halfWidth = ShileConstants.SHILE_WIDTH_BLOCKS / 2.0;
		double rightX = Math.cos(theta) * halfWidth;
		double rightZ = Math.sin(theta) * halfWidth;

		// 3x3 örnek noktası: sol/orta/sağ x alt/orta/üst.
		for (int column = -1; column <= 1; column++) {
			for (int row = 0; row <= 2; row++) {
				Vec3 point = new Vec3(
					centerX + rightX * column,
					bottomY + ShileConstants.SHILE_HEIGHT_BLOCKS * row / 2.0,
					centerZ + rightZ * column);
				if (isInFrustum(eye, point, forward, right, up, tanHorizontal, tanVertical)
					&& !isBlocked(player.level(), eye, point)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isInFrustum(Vec3 eye, Vec3 point, Vec3 forward, Vec3 right, Vec3 up,
									   double tanHorizontal, double tanVertical) {
		Vec3 offset = point.subtract(eye);
		double depth = offset.dot(forward);
		if (depth <= 0.01) {
			return false; // kameranın arkasında
		}
		return Math.abs(offset.dot(right)) / depth <= tanHorizontal
			&& Math.abs(offset.dot(up)) / depth <= tanVertical;
	}

	/** Göz ile nokta arasında görüşü kapatan blok var mı? (0,25 blokluk adımlarla yaklaşık kontrol) */
	private static boolean isBlocked(Level level, Vec3 from, Vec3 to) {
		Vec3 delta = to.subtract(from);
		int steps = (int) Math.ceil(delta.length() / OCCLUSION_STEP);
		for (int i = 1; i <= steps; i++) {
			Vec3 p = from.add(delta.scale(i / (double) steps));
			if (level.getBlockState(BlockPos.containing(p.x, p.y, p.z)).canOcclude()) {
				return true;
			}
		}
		return false;
	}
}
