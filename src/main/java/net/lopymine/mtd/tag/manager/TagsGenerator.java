package net.lopymine.mtd.tag.manager;

import com.google.common.collect.*;

import java.util.Iterator;

public class TagsGenerator implements Iterator<Character> {

	@SuppressWarnings("unused")
	private static final String TAG_MOD_CHARS = "0123456789abcdefghi";
	@SuppressWarnings("unused")
	private static final String UNUSED_TAG_MOD_CHARS = "56789abcdefghi";
	@SuppressWarnings("unused")
	private static final String MOD_CHARS_FOR_MODELS = "jklmnopqrstuvwxyz";
	@SuppressWarnings("unused")
	private static final String UNUSED_MOD_CHARS_FOR_MODELS = "uvwxyz";

	private static final String AVAILABLE_CHARS =
			"ABCDEFGHIJKLMNOPQRSTUV" +
			"WXYZ!#$%&'()*+,-./:;<=>?@[\\]^_`{}~¡¢£¤¥¦¨©ª«¬®¯" +
			"°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéê" +
			"ëìíîïðñòóôõö÷øùúûüýþÿĀāĂăĄąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĦħĨĩĪ" +
			"īĬĭĮįİıĲĳĴĵĶķĸĹĺĻļĽľĿŀŁłŃńŅņŇňŉŊŋŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧŨũŪūŬŭŮůŰ" +
			"űŲųŴŵŶŷŸŹźŻżŽžſƀƁƂƃƄƅƆƇƈƉƊƋƌƍƎƏƐƑƒƓƔƕƖƗƘƙƚƛƜƝƞƟƠơƢƣƤƥƦƧƨƩƪƫƬƭƮƯưƱƲƳ" +
			"ƴƵƶƷƸƹƺƻƼƽƾƿǀǁǂǃǄǅǆǇǈǉǊǋǌǍǎǏǐǑǒǓǔǕǖǗǘǙǚǛǜǝǞǟǠǡǢǣǤǥǦǧǨǩǪǫǬǭǮǯǰǱǲǳǴǵ";

	private final UnmodifiableIterator<Character> iterator;

	public TagsGenerator() {
		this.iterator = Lists.charactersOf(AVAILABLE_CHARS).iterator();
	}

	@Override
	public boolean hasNext() {
		return this.iterator.hasNext();
	}

	@Override
	public Character next() {
		return this.iterator.next();
	}
}
