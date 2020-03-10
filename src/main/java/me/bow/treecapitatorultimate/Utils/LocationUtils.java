package me.bow.treecapitatorultimate.Utils;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class LocationUtils {

    private static final double gravity = 0.1d;

    public static boolean isBlockEqual(Location one, Location two) {
        return
                one.getBlockX() == two.getBlockX()
                        && one.getBlockY() == two.getBlockY()
                        && one.getBlockZ() == two.getBlockZ();
    }

    public static boolean canIgnite(Material type) {
        return !type.isSolid() && !type.name().contains("WATER") && !type.name().contains("LAVA");
    }

    public static boolean isFinite(final Vector vec) {
        return Math.abs(vec.getX()) <= Double.MAX_VALUE && Math.abs(vec.getY()) <= Double.MAX_VALUE && Math.abs(vec.getZ()) <= Double.MAX_VALUE;
    }

    public static BlockFace getFacingDirection(double yaw, final boolean cardinal) {
        for (yaw += 180.0; yaw < 0.0; yaw += 360.0) {
        }
        while (yaw > 360.0) {
            yaw -= 360.0;
        }
        if (cardinal) {
            if (yaw >= 315.0 || yaw < 45.0) {
                return BlockFace.NORTH;
            }
            if (yaw >= 45.0 && yaw < 135.0) {
                return BlockFace.EAST;
            }
            if (yaw >= 135.0 && yaw < 225.0) {
                return BlockFace.SOUTH;
            }
            return BlockFace.WEST;
        } else {
            if (yaw >= 348.75 || yaw < 11.25) {
                return BlockFace.NORTH;
            }
            if (yaw >= 11.25 && yaw < 33.75) {
                return BlockFace.NORTH_NORTH_EAST;
            }
            if (yaw >= 33.75 && yaw < 56.25) {
                return BlockFace.NORTH_EAST;
            }
            if (yaw >= 56.25 && yaw < 78.75) {
                return BlockFace.EAST_NORTH_EAST;
            }
            if (yaw >= 78.75 && yaw < 101.25) {
                return BlockFace.EAST;
            }
            if (yaw >= 101.25 && yaw < 123.75) {
                return BlockFace.EAST_SOUTH_EAST;
            }
            if (yaw >= 123.75 && yaw < 146.25) {
                return BlockFace.SOUTH_EAST;
            }
            if (yaw >= 146.25 && yaw < 168.75) {
                return BlockFace.SOUTH_SOUTH_EAST;
            }
            if (yaw >= 168.75 && yaw < 191.25) {
                return BlockFace.SOUTH;
            }
            if (yaw >= 191.25 && yaw < 213.75) {
                return BlockFace.SOUTH_SOUTH_WEST;
            }
            if (yaw >= 213.75 && yaw < 236.25) {
                return BlockFace.SOUTH_WEST;
            }
            if (yaw >= 236.25 && yaw < 258.75) {
                return BlockFace.WEST_SOUTH_WEST;
            }
            if (yaw >= 258.75 && yaw < 281.25) {
                return BlockFace.WEST;
            }
            if (yaw >= 281.25 && yaw < 303.75) {
                return BlockFace.WEST_NORTH_WEST;
            }
            if (yaw >= 303.75 && yaw < 326.25) {
                return BlockFace.NORTH_WEST;
            }
            return BlockFace.NORTH_NORTH_WEST;
        }
    }

    public static double getYaw(final BlockFace facing, final boolean cardinal) {
        if (cardinal) {
            if (facing == BlockFace.NORTH) {
                return 180.0;
            }
            if (facing == BlockFace.EAST) {
                return 270.0;
            }
            if (facing == BlockFace.SOUTH) {
                return 360.0;
            }
            return 450.0;
        } else {
            if (facing == BlockFace.NORTH) {
                return 180.0;
            }
            if (facing == BlockFace.NORTH_NORTH_EAST) {
                return 202.5;
            }
            if (facing == BlockFace.NORTH_EAST) {
                return 225.0;
            }
            if (facing == BlockFace.EAST_NORTH_EAST) {
                return 247.5;
            }
            if (facing == BlockFace.EAST) {
                return 270.0;
            }
            if (facing == BlockFace.EAST_SOUTH_EAST) {
                return 292.5;
            }
            if (facing == BlockFace.SOUTH_EAST) {
                return 315.0;
            }
            if (facing == BlockFace.SOUTH_SOUTH_EAST) {
                return 337.5;
            }
            if (facing == BlockFace.SOUTH) {
                return 360.0;
            }
            if (facing == BlockFace.SOUTH_SOUTH_WEST) {
                return 382.5;
            }
            if (facing == BlockFace.SOUTH_WEST) {
                return 405.0;
            }
            if (facing == BlockFace.WEST_SOUTH_WEST) {
                return 427.5;
            }
            if (facing == BlockFace.WEST) {
                return 450.0;
            }
            if (facing == BlockFace.WEST_NORTH_WEST) {
                return 472.5;
            }
            if (facing == BlockFace.NORTH_WEST) {
                return 495.0;
            }
            return 517.5;
        }
    }

    public static Location getLocationInFront(final Location loc, final double distance, final boolean includeY) {
        double angle;
        for (angle = loc.getYaw(), angle += 90.0; angle < 0.0; angle += 360.0) {
        }
        while (angle > 360.0) {
            angle -= 360.0;
        }
        angle = angle * Math.PI / 180.0;
        final double sin = Math.sin(angle);
        return new Location(loc.getWorld(), loc.getX() + distance * Math.cos(angle), includeY ? (loc.getY() + distance * sin * sin) : loc.getY(), loc.getZ() + distance * sin);
    }

    public static Location getLocationBehind(final Location loc, final double distance, final boolean includeY) {
        double angle;
        for (angle = loc.getYaw(), angle += 270.0; angle < 0.0; angle += 360.0) {
        }
        while (angle > 360.0) {
            angle -= 360.0;
        }
        angle = angle * Math.PI / 180.0;
        final double sin = Math.sin(angle);
        return new Location(loc.getWorld(), loc.getX() + distance * Math.cos(angle), includeY ? (loc.getY() + distance * sin * sin) : loc.getY(), loc.getZ() + distance * sin);
    }

    public static Location getLocationAtAngle(final Location center, final double distance, final double dregreeOffsetFromFacingDirection, final boolean includeY) {
        double angle;
        for (angle = center.getYaw(), angle += 90.0 + dregreeOffsetFromFacingDirection; angle < 0.0; angle += 360.0) {
        }
        while (angle > 360.0) {
            angle -= 360.0;
        }
        angle = angle * Math.PI / 180.0;
        final double sin = Math.sin(angle);
        return new Location(center.getWorld(), center.getX() + distance * Math.cos(angle), includeY ? (center.getY() + distance * sin * sin) : center.getY(), center.getZ() + distance * sin);
    }

    public static Location getRandomPointAround(final Location loc, final double radius, final boolean includeY) {
        final double angle = Math.random() * Math.PI * 2.0;
        final double sin = Math.sin(angle);
        return new Location(loc.getWorld(), loc.getX() + radius * Math.cos(angle), includeY ? (loc.getY() + radius * sin * sin) : loc.getY(), loc.getZ() + radius * sin);
    }

    public static Location[] getHalfCircleAround(final Location loc, final double radius, final int numPoints) {
        final Location[] retVal = new Location[numPoints];
        final double piSlice = Math.PI / numPoints;
        double angle;
        for (angle = loc.getYaw(); angle < 0.0; angle += 360.0) {
        }
        while (angle > 360.0) {
            angle -= 360.0;
        }
        angle = angle * Math.PI / 180.0;
        for (int i = 0; i < numPoints; ++i) {
            final double newAngle = angle + piSlice * i;
            retVal[i] = new Location(loc.getWorld(), loc.getX() + radius * Math.cos(newAngle), loc.getY(), loc.getZ() + radius * Math.sin(newAngle));
        }
        return retVal;
    }

    public static Location[] getCircleAround(final Location loc, final double radius, final int numPoints) {
        final Location[] retVal = new Location[numPoints];
        final double piSlice = (2 * Math.PI) / numPoints;
        for (int i = 0; i < numPoints; ++i) {
            final double angle = piSlice * i;
            retVal[i] = new Location(loc.getWorld(), loc.getX() + radius * Math.cos(angle), loc.getY(), loc.getZ() + radius * Math.sin(angle));
        }
        return retVal;
    }

    public static Location toBlockLocation(final Location loc) {
        return new Location(loc.getWorld(), (double) loc.getBlockX(), (double) loc.getBlockY(), (double) loc.getBlockZ());
    }

    public static Vector moveSmoothly(final Location from, final Location to) {
        return moveSmoothly(from, to, 2.5);
    }

    public static Vector moveSmoothly(final Location from, final Location to, final double time) {
        final double x = to.getX() - from.getX();
        final double y = to.getY() - from.getY();
        final double z = to.getZ() - from.getZ();
        return new Vector(getTargetVelocity(x, 0.0, time), getTargetVelocity(y, gravity, time), getTargetVelocity(z, 0.0, time));
    }

    public static boolean areEqualXYZ(final Location from, final Location to) {
        return areEqualXYZ(from, to, 0.0);
    }

    public static boolean areEqualXYZ(final Location from, final Location to, final double epsilon) {
        return from.getWorld().equals(to.getWorld()) && from.distanceSquared(to) <= epsilon * epsilon;
    }

    public static Location makeEqualXYZ(final Location from, Location to) {
        to = to.clone();
        to.setWorld(from.getWorld());
        to.setX(from.getX());
        to.setY(from.getY());
        to.setZ(from.getZ());
        return to;
    }

    public static boolean areEqualPitchYaw(final Location from, final Location to) {
        return areEqualPitchYaw(from, to, 0.0);
    }

    public static boolean areEqualPitchYaw(final Location from, final Location to, final double epsilon) {
        return Math.abs(from.getPitch() - to.getPitch()) <= epsilon && Math.abs(from.getYaw() - to.getYaw()) <= epsilon;
    }

    private static double getTargetVelocity(double d, double a, final double t) {
        a *= -0.5;
        a *= Math.pow(t, 2.0);
        d -= a;
        return 2.0 * (d / t);
    }
}