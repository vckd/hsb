/**
 * This class is generated by jOOQ
 */
package sample.jooq.domain;


import javax.annotation.Generated;

import org.jooq.Sequence;
import org.jooq.impl.SequenceImpl;


/**
 * Convenience access to all sequences in public
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Sequences {

	/**
	 * The sequence <code>public.playground_equip_id_seq</code>
	 */
	public static final Sequence<Long> PLAYGROUND_EQUIP_ID_SEQ = new SequenceImpl<Long>("playground_equip_id_seq", Public.PUBLIC, org.jooq.impl.SQLDataType.BIGINT.nullable(false));
}
