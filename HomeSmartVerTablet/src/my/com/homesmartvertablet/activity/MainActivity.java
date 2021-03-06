package my.com.homesmartvertablet.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.homesmartvertablet.activity.R;

import my.com.homesmartvertablet.adapter.ListDeviceAdapter;
import my.com.homesmartvertablet.adapter.ListRoomAdapter;
import my.com.homesmartvertablet.controller.DeviceItemController;
import my.com.homesmartvertablet.model.DeviceItem;
import my.com.homesmartvertablet.model.RoomItem;
import my.com.homesmartvertablet.utils.CommonFunctions;
import my.com.homesmartvertablet.utils.PreferenceHelper;

import android.R.color;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
	private List<RoomItem> listRoom;
	private ListRoomAdapter roomAdapter;
	private ListDeviceAdapter deviceAdapter;
	private ListView listviewRoomHouse;
	private ListView listviewDeviceItem;
	private List<DeviceItem> listDeviceItem;
	private static int pos = 0;
	private static int posRoom =0;
	public static String phoneNumberDefault ;
	private RelativeLayout overlay;
	private int checkOverlay = 0;
	private static int lastClickId = -1;
	private int flag_on_off_all_devices = 0;
	private  DeviceItem items;
	private Button btnSwitch;
	//update check-out my svn
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnSwitch = (Button)findViewById(R.id.on_off_all_option);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		//getActionBar().setHomeButtonEnabled(true);
		
		// set background color for actionbar
		Drawable d = getResources().getDrawable(R.drawable.background_3);  
		getActionBar().setBackgroundDrawable(d);
//		ColorDrawable colorDrawable = new ColorDrawable();
//		colorDrawable.setColor(0xff00ddff);
//		getActionBar().setBackgroundDrawable(colorDrawable);
		
		listviewRoomHouse = (ListView)findViewById(R.id.main_list_room);
		listviewRoomHouse.setSelector(R.drawable.list_selector);
		listviewDeviceItem = (ListView)findViewById(R.id.main_list_device);
		overlay = (RelativeLayout)findViewById(R.id.relative_overlay_instruction);
		
		PreferenceHelper.getInstance(this);
		PreferenceHelper.putInteger("CHECK_OVERLAY", 0);
		checkOverlay = PreferenceHelper.getInteger("CHECK_OVERLAY", 0);
		
		//get phone default number
		phoneNumberDefault = PreferenceHelper.getString("PHONE_NUMBER_DEVICE", "");
		if(checkOverlay == 1){
			overlay.setVisibility(View.INVISIBLE);
		}
		
		overlay.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				PreferenceHelper.putInteger("CHECK_OVERLAY", 1);
				overlay.setVisibility(View.INVISIBLE);
				return false;
			}
		});
		final String[] roomName = new String[]{"Living Room","Bed Room","Kitchen Room","Toilet"};
		
		listRoom = new ArrayList<RoomItem>();
		for(int i =0; i < roomName.length; i ++){
			RoomItem item = new RoomItem(i, roomName[i]);
			listRoom.add(item);
		}
		
		roomAdapter = new ListRoomAdapter(this, R.layout.inflater_item_of_list_room, listRoom);
		listviewRoomHouse.setAdapter(roomAdapter);
		
		listviewRoomHouse.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, roomName[position] + "", Toast.LENGTH_SHORT).show();
				try {
					posRoom = position;
					listDeviceItem = DeviceItemController.getInstance(MainActivity.this).getListDeviceOfRoomByRoomID(position);
					Log.w("NUMBER_ITEM",listDeviceItem.size()+"");
					deviceAdapter = new ListDeviceAdapter(MainActivity.this, R.layout.inflater_item_of_list_device, listDeviceItem);
					listviewDeviceItem.setAdapter(deviceAdapter);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	
		listviewRoomHouse.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.w("POSR_ROOM", posRoom + "");
				if (listviewRoomHouse.getFirstVisiblePosition() > posRoom | listviewRoomHouse.getLastVisiblePosition() <= posRoom) {
					listviewRoomHouse.smoothScrollToPosition(posRoom - 1);
				}
			}
		}, 1000L);
		listviewDeviceItem.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				//listviewDeviceItem.setSelection(firstVisibleItem);
				listviewDeviceItem.invalidateViews();
			}
		});
		
		// create list item of living room
		// 0-living room, 1 - bed room, 2-kitchen room, 3-toilet
		// 0 - lamp, 1 -fan, 2-window gara, 3-camera,4-tv
		// total is 10 port
//		for(int i =0; i < 6; i++){
//			DeviceItem item = new DeviceItem(0,"Device " + i ,0, i,0);
//			DeviceItem item1 = new DeviceItem(1,"Device " + i ,1, 5 + i,1);
//			DeviceItem item2 = new DeviceItem(2,"Device " + i ,2, 10 + i,0);
//			DeviceItem item3 = new DeviceItem(3,"Device " + i ,3, 15 + i,1);
//			try {
//				DeviceItemController.getInstance(MainActivity.this).createDeviceItem(item);
//				DeviceItemController.getInstance(MainActivity.this).createDeviceItem(item1);
//				DeviceItemController.getInstance(MainActivity.this).createDeviceItem(item2);
//				DeviceItemController.getInstance(MainActivity.this).createDeviceItem(item3);
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		registerForContextMenu(listviewDeviceItem);
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.main_list_device){
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			pos = info.position;
			menu.setHeaderTitle(deviceAdapter.getItem(info.position).getDeviceName());
			String[] menuItems = getResources().getStringArray(R.array.context_menu);
			 for (int i = 0; i<menuItems.length; i++) {
			      menu.add(Menu.NONE, i, i, menuItems[i]);
			 }
		}
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		refesh();
		super.onResume();
		
	}
	public void refesh(){
		try {
			listDeviceItem = DeviceItemController.getInstance(MainActivity.this).getListDeviceOfRoomByRoomID(posRoom);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		deviceAdapter = new ListDeviceAdapter(MainActivity.this, R.layout.inflater_item_of_list_device, listDeviceItem);
		listviewDeviceItem.setAdapter(deviceAdapter);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
			case 0:{
				Intent intent = new Intent(MainActivity.this,ModifyDeviceItemActivity.class);
				intent.putExtra("device_id", listDeviceItem.get(pos).getDeviceID());
				intent.putExtra("room_id", listDeviceItem.get(pos).getRoomID());
				intent.putExtra("device_type", listDeviceItem.get(pos).getDeviceType());
				intent.putExtra("device_name", listDeviceItem.get(pos).getDeviceName());
				intent.putExtra("device_port", listDeviceItem.get(pos).getDevicePort());
				intent.putExtra("device_status", listDeviceItem.get(pos).getStatus());
				startActivityForResult(intent,2);
				break;
			}
			case 1:{
				showDialog(MainActivity.this,pos);
				break;
			}
		}
		return true;
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
			case android.R.id.home: {
//				Intent upIntent = new Intent(this, DisplayFragmentActivity.class);
//				if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
//					// This activity is not part of the application's task, so
//					// create a new task
//					// with a synthesized back stack.
//					TaskStackBuilder.from(this)
//							.addNextIntent(new Intent(this, MainActivity.class))
//							.addNextIntent(upIntent).startActivities();
//					finish();
//				} else {
//					// This activity is part of the application's task, so simply
//					// navigate up to the hierarchical parent activity.
//					NavUtils.navigateUpTo(this, upIntent);
//				}
				break;
			}
			case R.id.add_option:{
				Toast.makeText(MainActivity.this, "ADD", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(MainActivity.this,AddDeviceItemActivity.class);
				intent.putExtra("room_id", posRoom);
				startActivityForResult(intent,1);
				break;
			}
			case R.id.reset_option:
			{
				Toast.makeText(MainActivity.this, "SYNC", Toast.LENGTH_SHORT).show();
				break;
			}
			case R.id.about_option:{
				Toast.makeText(MainActivity.this, "ABOUT", Toast.LENGTH_SHORT).show();
				break;
			}
			case R.id.action_settings:{
				Toast.makeText(MainActivity.this, "SETTINGS", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(MainActivity.this, PreferenceActivity.class);
				startActivity(intent);
				break;
			}
			case R.id.on_off_all_option:{
				flag_on_off_all_devices = flag_on_off_all_devices ^ 1;
				
				switch(flag_on_off_all_devices){
					case 0:{
						item.setIcon(R.drawable.switch_off);
						item.setTitle("OFF");
						Toast.makeText(this, "Turn off all devices!", Toast.LENGTH_SHORT).show();
						Switch("Tat@a", 0);
						refesh();
						break;
					}
					case 1:{
						item.setIcon(R.drawable.switch_on);
						item.setTitle("ON");
						Toast.makeText(this, "Turn on all devices!", Toast.LENGTH_SHORT).show();
						Switch("Bat@a", 1);
						refesh();
						break;
					}
				}
				break;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
			case 1:{
				if(resultCode == RESULT_OK){
					int ROOM_ID = data.getExtras().getInt("ROOM_ID");
					String NAME_DEVICE = data.getExtras().getString("NAME_DEVICE");
					int TYPE_DEVICE = data.getExtras().getInt("TYPE_DEVICE");
					int PORT_DEVICE = data.getExtras().getInt("PORT_DEVICE");
					int STATUS_DEVICE = data.getExtras().getInt("STATUS_DEVICE");
					DeviceItem item = new DeviceItem(ROOM_ID, NAME_DEVICE, TYPE_DEVICE, PORT_DEVICE, STATUS_DEVICE);
					listDeviceItem.add(item);
					deviceAdapter = new ListDeviceAdapter(MainActivity.this, R.layout.inflater_item_of_list_device, listDeviceItem);
					listviewDeviceItem.setAdapter(deviceAdapter);
					//deviceAdapter.notifyDataSetChanged();
				}
				break;
			}
			case 2:{
				if(resultCode == RESULT_OK){
					int ROOM_ID = data.getExtras().getInt("ROOM_ID");
					String NAME_DEVICE = data.getExtras().getString("NAME_DEVICE");
					int TYPE_DEVICE = data.getExtras().getInt("TYPE_DEVICE");
					int PORT_DEVICE = data.getExtras().getInt("PORT_DEVICE");
					int STATUS_DEVICE = data.getExtras().getInt("STATUS_DEVICE");
					DeviceItem item = new DeviceItem(ROOM_ID, NAME_DEVICE, TYPE_DEVICE, PORT_DEVICE, STATUS_DEVICE);
					deviceAdapter.remove(deviceAdapter.getItem(pos));
					deviceAdapter.insert(item, pos);
					deviceAdapter.notifyDataSetChanged();
				}
				
			}
		}
	}

	public void showDialog(final Context context, final int pos){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		builder.setTitle("");
		builder.setMessage("Do you want delete "+ deviceAdapter.getItem(pos).getDeviceName() + " ?" );
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// TODO Auto-generated method stub
				try {
					DeviceItemController.getInstance(context).deleteDeviceItemByID(deviceAdapter.getItem(pos).getDeviceID());
					listDeviceItem.remove(pos);
					deviceAdapter = new ListDeviceAdapter(MainActivity.this, R.layout.inflater_item_of_list_device, listDeviceItem);
					listviewDeviceItem.setAdapter(deviceAdapter);
					//deviceAdapter.notifyDataSetChanged();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		
	}
	public void Switch(String command, int status){
		try {
			// send mess turn off all devices
			CommonFunctions.sendMess(MainActivity.phoneNumberDefault, command);
			// then save status on database
			// get all devices
			
				listDeviceItem = DeviceItemController.getInstance(this).getAllDeviceItem();
				for(int i =0; i < listDeviceItem.size(); i++){
					items = listDeviceItem.get(i);
					items.setStatus(status);
					DeviceItemController.getInstance(this).updateDeviceItemByID(items, items.getDeviceID());
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

}
