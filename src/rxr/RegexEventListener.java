package rxr;

interface RegexEventListener
{
	public enum Type
	{
		RECALC_START, RECALC_COMPLETE, BAD_PATTERN,
	};

	public void regexEvent(RegexEventListener.Type t);
}