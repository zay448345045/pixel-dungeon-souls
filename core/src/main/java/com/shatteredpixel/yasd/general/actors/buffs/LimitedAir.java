/*
 *
 *   Pixel Dungeon
 *   Copyright (C) 2012-2015 Oleg Dolya
 *
 *   Shattered Pixel Dungeon
 *   Copyright (C) 2014-2019 Evan Debenham
 *
 *   Powered Pixel Dungeon
 *   Copyright (C) 2014-2020 Samuel Braithwaite
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *
 */

package com.shatteredpixel.yasd.general.actors.buffs;

import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.Element;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.utils.GLog;
import com.watabou.utils.Bundle;

public class LimitedAir extends Buff {
	{
		element = Element.WATER;
	}

	public static final float DURATION = 18f;

	private float duration = DURATION;

	public static float percentage(Char ch) {
		LimitedAir air = ch.buff(LimitedAir.class);
		if (air == null) {
			return 1f;
		} else {
			return air.percentage();
		}
	}

	public float percentage() {
		return duration/DURATION;
	}

	public void reset() {
		duration = DURATION;
	}

	private static boolean needed(Char ch) {
		return Dungeon.underwater() &&
				!ch.properties().contains(Char.Property.WATERY) &&  //Piranahs and Jellyfish are immune of course
				!ch.properties().contains(Char.Property.UNDEAD) &&  //We don't want Skeletons drowning...
				!ch.properties().contains(Char.Property.INORGANIC); //Same for Golems
	}

	@Override
	public boolean act() {
		if (!Dungeon.level.canBreathe(target.pos)) {
			if (duration > 0) {
				duration--;
			} else {
				target.damage(target.HT / 5, this);
				if (target == Dungeon.hero && !target.isAlive()) {
					Dungeon.fail(getClass());
					GLog.n(Messages.get(this, "ondeath"));
				}
			}
		}
		spend( TICK );
		return true;
	}

	public static void updateBuff(Char ch) {
		LimitedAir air = ch.buff(LimitedAir.class);
		if (LimitedAir.needed(ch) & air == null) {
			Buff.affect(ch, LimitedAir.class).reset();
		} else if (!LimitedAir.needed(ch) && air != null) {
			air.detach();
		}
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	private static final String DURATION_KEY = "duration";

	@Override
	public void storeInBundle( Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DURATION_KEY, duration);
	}

	@Override
	public void restoreFromBundle( Bundle bundle) {
		super.restoreFromBundle(bundle);
		duration = bundle.getFloat(DURATION_KEY);
	}
}
