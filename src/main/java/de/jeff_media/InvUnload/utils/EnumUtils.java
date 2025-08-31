/*
 * Copyright (c) 2023. JEFF Media GbR / mfnalex et al.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.jeff_media.InvUnload.utils;

import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;

public final class EnumUtils {
    /**
     * Gets an EnumSet of the given Enum constants by a list of regex patterns. Example:
     * <pre>
     * materials:
     * - "^((.+)_)*CHEST$" # matches CHEST, TRAPPED_CHEST, etc
     * - "^((.+)_)*SHULKER_BOX$" # matches SHULKER_BOX, RED_SHULKER_BOX, etc
     * - "^BARREL$" # matches only BARREL
     * </pre>
     */
    public static <E extends Enum<E>> EnumSet<E> getEnumsFromRegexList(final Class<E> enumClazz, final List<String> list) {
        final EnumSet<E> result = EnumSet.noneOf(enumClazz);
        for (final String regex : list) {
            final Pattern pattern = Pattern.compile(regex);
            for (final E e : enumClazz.getEnumConstants()) {
                if (result.contains(e)) continue;
                final String name = e.name();
                if (pattern.matcher(name).matches()) {
                    result.add(e);
                }
            }
        }
        return result;
    }

    public static boolean soundExists(String value) {
        for(Sound sound: Sound.values()) {
            if(sound.name().equalsIgnoreCase(value)) return true;
        }
        return false;
    }

    public static boolean particleExists(String value) {
        for(Particle particle: Particle.values()) {
            if(particle.name().equalsIgnoreCase(value)) return true;
        }
        return false;
    }
}
