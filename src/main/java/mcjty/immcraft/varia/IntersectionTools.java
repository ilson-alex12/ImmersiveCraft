package mcjty.immcraft.varia;

import mcjty.immcraft.api.util.Vector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.List;

public class IntersectionTools {

    private static final float EPSILON = 0.001f;

    public static float calculateRayToLineDistance(Vector rayOrigin, Vector rayVec, Vector lineStart, Vector lineEnd) {
        Vector u = rayVec;
        Vector v = Vector.subtract(lineEnd, lineStart);
        Vector w = Vector.subtract(rayOrigin, lineStart);
        float a = Vector.dot(u, u);    // always >= 0
        float b = Vector.dot(u, v);
        float c = Vector.dot(v, v);    // always >= 0
        float d = Vector.dot(u, w);
        float e = Vector.dot(v, w);
        float D = a * c - b * b;    // always >= 0
        float sc, sN, sD = D;    // sc = sN / sD, default sD = D >= 0
        float tc, tN, tD = D;    // tc = tN / tD, default tD = D >= 0

        // compute the line parameters of the two closest points
        if (D < EPSILON) {    // the lines are almost parallel
            sN = 0.0f;            // force using point P0 on segment S1
            sD = 1.0f;            // to prevent possible division by 0.0 later
            tN = e;
            tD = c;
        } else {                // get the closest points on the infinite lines
            sN = (b * e - c * d);
            tN = (a * e - b * d);
            if (sN < 0.0f) {    // sc < 0 => the s=0 edge is visible
                sN = 0.0f;
                tN = e;
                tD = c;
            }
        }

        if (tN < 0.0f) {        // tc < 0 => the t=0 edge is visible
            tN = 0.0f;
            // recompute sc for this edge
            if (-d < 0.0f) {
                sN = 0.0f;
            } else {
                sN = -d;
                sD = a;
            }
        } else if (tN > tD) {      // tc > 1 => the t=1 edge is visible
            tN = tD;
            // recompute sc for this edge
            if ((-d + b) < 0.0) {
                sN = 0;
            } else {
                sN = (-d + b);
                sD = a;
            }
        }
        // finally do the division to get sc and tc
        sc = (Math.abs(sN) < EPSILON ? 0.0f : sN / sD);
        tc = (Math.abs(tN) < EPSILON ? 0.0f : tN / tD);

        // get the difference of the two closest points
        Vector dP = Vector.subtract(Vector.add(w, Vector.mul(u, sc)), Vector.mul(v, tc));
//        Vector dP = w + (sc * u) - (tc * v);	// = S1(sc) - S2(tc)
//        info.iFracRay = sc;
//        info.iFracLine = tc;
        return (float) dP.length();
    }

    public static MovingObjectPosition getMovingObjectPositionFromPlayer(World world, EntityPlayer player, boolean stopOnLiquid) {
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double doubleX = player.prevPosX + (player.posX - player.prevPosX) * (double) f;
        double doubleY = player.prevPosY + (player.posY - player.prevPosY) * (double) f + (double) (world.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
        double doubleZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f;
        Vec3 start = new Vec3(doubleX, doubleY, doubleZ);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 5.0D;
        if (player instanceof EntityPlayerMP) {
            d3 = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 end = start.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
        return world.rayTraceBlocks(start, end, stopOnLiquid, !stopOnLiquid, false);
    }

    public static List<MovingObjectPosition> rayTest(World world, Vec3 start, Vec3 end, boolean stopOnLiquid, boolean ignoreBlocksWithoutBoundingbox, boolean returnLastUncollidableBlock) {
//        if (Double.isNaN(start.xCoord) || Double.isNaN(start.yCoord) || Double.isNaN(start.zCoord)) {
//            return Collections.emptyList();
//        }
//        if (Double.isNaN(end.xCoord) || Double.isNaN(end.yCoord) || Double.isNaN(end.zCoord)) {
//            return Collections.emptyList();
//        }
//
//        int endx = MathHelper.floor_double(end.xCoord);
//        int endy = MathHelper.floor_double(end.yCoord);
//        int endz = MathHelper.floor_double(end.zCoord);
//        int startx = MathHelper.floor_double(start.xCoord);
//        int starty = MathHelper.floor_double(start.yCoord);
//        int startz = MathHelper.floor_double(start.zCoord);
//
//        BlockPos startPos = new BlockPos(startx, starty, startz);
//        IBlockState startState = world.getBlockState(startPos);
//        Block block = startState.getBlock();
////        int meta = world.getBlockMetadata(startx, starty, startz);
//
//        List<MovingObjectPosition> positions = new ArrayList<>();
//
//        if ((!ignoreBlocksWithoutBoundingbox || block.getCollisionBoundingBox(world, startPos, startState) != null) && block.canCollideCheck(startState, stopOnLiquid)) {
//            MovingObjectPosition movingobjectposition = block.collisionRayTrace(world, startPos, start, end);
//
//            if (movingobjectposition != null) {
//                positions.add(movingobjectposition);
//                if (positions.size() > 1) {
//                    return positions;
//                }
//            }
//        }
//
//        MovingObjectPosition movingobjectposition2 = null;
//        int cnt = 200;
//
//        while (cnt-- >= 0) {
//            if (Double.isNaN(start.xCoord) || Double.isNaN(start.yCoord) || Double.isNaN(start.zCoord)) {
//                return positions;
//            }
//
//            if (startx == endx && starty == endy && startz == endz) {
//                if (returnLastUncollidableBlock && movingobjectposition2 != null) {
//                    positions.add(movingobjectposition2);
//                    if (positions.size() > 1) {
//                        return positions;
//                    }
//                }
//            }
//
//            boolean flag6 = true;
//            boolean flag3 = true;
//            boolean flag4 = true;
//            double d0 = 999.0D;
//            double d1 = 999.0D;
//            double d2 = 999.0D;
//
//            if (endx > startx) {
//                d0 = (double) startx + 1.0D;
//            } else if (endx < startx) {
//                d0 = (double) startx + 0.0D;
//            } else {
//                flag6 = false;
//            }
//
//            if (endy > starty) {
//                d1 = (double) starty + 1.0D;
//            } else if (endy < starty) {
//                d1 = (double) starty + 0.0D;
//            } else {
//                flag3 = false;
//            }
//
//            if (endz > startz) {
//                d2 = (double) startz + 1.0D;
//            } else if (endz < startz) {
//                d2 = (double) startz + 0.0D;
//            } else {
//                flag4 = false;
//            }
//
//            double d3 = 999.0D;
//            double d4 = 999.0D;
//            double d5 = 999.0D;
//            double d6 = end.xCoord - start.xCoord;
//            double d7 = end.yCoord - start.yCoord;
//            double d8 = end.zCoord - start.zCoord;
//
//            if (flag6) {
//                d3 = (d0 - start.xCoord) / d6;
//            }
//
//            if (flag3) {
//                d4 = (d1 - start.yCoord) / d7;
//            }
//
//            if (flag4) {
//                d5 = (d2 - start.zCoord) / d8;
//            }
//
//            byte b0;
//
//            if (d3 < d4 && d3 < d5) {
//                if (endx > startx) {
//                    b0 = 4;
//                } else {
//                    b0 = 5;
//                }
//
//                start = new Vec3(d0, start.yCoord + d7 * d3, start.zCoord + d8 * d3);
//            } else if (d4 < d5) {
//                if (endy > starty) {
//                    b0 = 0;
//                } else {
//                    b0 = 1;
//                }
//
//                start = new Vec3(start.xCoord + d6 * d4, d1, start.zCoord + d8 * d4);
//            } else {
//                if (endz > startz) {
//                    b0 = 2;
//                } else {
//                    b0 = 3;
//                }
//
//                start = new Vec3(start.xCoord + d6 * d5, start.yCoord + d7 * d5, d2);
//            }
//
//            Vec3 vec32 = new Vec3(start.xCoord, start.yCoord, start.zCoord);
//
//            startx = (int) (vec32.xCoord = (double) MathHelper.floor_double(start.xCoord));
//
//            if (b0 == 5) {
//                --startx;
//                ++vec32.xCoord;
//            }
//
//            starty = (int) (vec32.yCoord = (double) MathHelper.floor_double(start.yCoord));
//
//            if (b0 == 1) {
//                --starty;
//                ++vec32.yCoord;
//            }
//
//            startz = (int) (vec32.zCoord = (double) MathHelper.floor_double(start.zCoord));
//
//            if (b0 == 3) {
//                --startz;
//                ++vec32.zCoord;
//            }
//
//            Block block1 = world.getBlock(startx, starty, startz);
//            meta = world.getBlockMetadata(startx, starty, startz);
//
//            if (!ignoreBlocksWithoutBoundingbox || block1.getCollisionBoundingBoxFromPool(world, startx, starty, startz) != null) {
//                if (block1.canCollideCheck(meta, stopOnLiquid)) {
//                    MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(world, startx, starty, startz, start, end);
//
//                    if (movingobjectposition1 != null) {
//                        positions.add(movingobjectposition1);
//                        if (positions.size() > 1) {
//                            return positions;
//                        }
//                    }
//                } else {
//                    movingobjectposition2 = new MovingObjectPosition(startx, starty, startz, b0, start, false);
//                }
//            }
//        }
//
//        if (returnLastUncollidableBlock && movingobjectposition2 != null) {
//            positions.add(movingobjectposition2);
//        }
//        return positions;
        return null;
    }

    public static Vector intersectAtGrid(BlockPos c1, BlockPos c2, Vector v1, Vector v2) {
        if (c1.getX() == c2.getX() && c1.getZ() == c2.getZ()) {
            float yval;
            if (c1.getY() < c2.getY()) {
                yval = c2.getY();
            } else {
                yval = c1.getY();
            }
            float r = (yval - v1.y) / (v2.y - v1.y);
            return new Vector(r * (v2.x - v1.x) + v1.x, yval, r * (v2.z - v1.z) + v1.z);
        } else if (c1.getX() == c2.getX() && c1.getY() == c2.getY()) {
            float zval;
            if (c1.getZ() < c2.getZ()) {
                zval = c2.getZ();
            } else {
                zval = c1.getZ();
            }
            float r = (zval - v1.z) / (v2.z - v1.z);
            return new Vector(r * (v2.x - v1.x) + v1.x, r * (v2.y - v1.y) + v1.y, zval);
        } else if (c1.getY() == c2.getY() && c1.getZ() == c2.getZ()) {
            float xval;
            if (c1.getX() < c2.getX()) {
                xval = c2.getX();
            } else {
                xval = c1.getX();
            }
            float r = (xval - v1.x) / (v2.x - v1.x);
            return new Vector(xval, r * (v2.y - v1.y) + v1.y, r * (v2.z - v1.z) + v1.z);
        } else {
            return new Vector((v1.x + v2.x) / 2, (v1.y + v2.y) / 2, (v1.z + v2.z) / 2);
        }

    }
}
