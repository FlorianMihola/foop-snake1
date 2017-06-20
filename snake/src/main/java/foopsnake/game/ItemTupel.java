package foopsnake.game;

public class ItemTupel {
	private Item item;
	private long time;
	
	public ItemTupel(Item item, long time) {
		this.item = item;
		this.time = time;
	}
	
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	
}
