package rxr;

/**
 * A regex event occurs whenever a new matching operation begins, or when one
 * finishes, whether it succeeds or finishes. In each case, the event type will
 * be different.
 */
interface RegexEventListener
{
	public enum Type
	{
		RECALC_START, RECALC_COMPLETE, BAD_PATTERN, BAD_REPLACE, RECALC_PROGRESS,
	};

	public void regexEvent(Type t);
}