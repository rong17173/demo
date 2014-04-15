package common.events
{
	import flash.events.Event;
	
	public class CustomEvent extends Event
	{
		public var data:Object;
		public function CustomEvent(type:String, data:Object = null, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type);
			this.data = data;
		}
		
		override public function clone():CustomEvent
		{
			// TODO Auto Generated method stub
			return new CustomEvent(type,data,bubbles,cancelable);
		}
		
		
	}
}