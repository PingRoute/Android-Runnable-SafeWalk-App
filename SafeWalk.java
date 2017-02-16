package edu.purdue.cs.cs180.safewalk;

import java.util.ArrayList;

import edu.cs.purdue.cs.cs180.channel.ClientConnection;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import edu.cs.purdue.cs.cs180.channel.MessageListener;

public class SafeWalk extends Activity implements MessageListener {

	// TODO: declare the connection here.
	ClientConnection clientConnection;
	// TODO: declare the cipher here.
	Cipher cipher;
	Handler mHandler = null;
	String s = "wang1002";
	private SafeWalk safeWalkApp = this;

	/** Called when the activity is first created. */
	@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);

			// TODO: create the connection and set the message listener.
			clientConnection  = new ClientConnection("pc.cs.purdue.edu", 12190);
			cipher = new Cipher("DXsUn");
			clientConnection.setMessageListener(this);
			final Button locationRefreshButton = (Button) findViewById(R.id.locationRefresh);
			final EditText locationTextView = (EditText) findViewById(R.id.locationText);
			final Spinner locationsSpinner = (Spinner) findViewById(R.id.moveSpinner);
			final Button escortRefreshButton = (Button) findViewById(R.id.requestsRefresh);
			final Button scoreRefreshButton = (Button) findViewById(R.id.scoreRefresh);
			final EditText scoreTextView = (EditText) findViewById(R.id.scoreText);
			final Button distancesRefreshButton = (Button) findViewById(R.id.distancesRefresh);
			final Spinner escortsSpinner = (Spinner) findViewById(R.id.escortSpinner);
			final Button moveSubmit = (Button) findViewById(R.id.submitMove);
			final Button escortSubmit = (Button) findViewById(R.id.submitEscort);

			locationTextView.setEnabled(false);

			locationRefreshButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
					// TODO: send a get location message.
					String a = s + cipher.encrypt(GET_CURRENT_LOCATION);
					clientConnection.sendMessage(a);

					}
					});

			scoreTextView.setEnabled(false);

			scoreRefreshButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
					// TODO: send a get score message.
					String b = s + cipher.encrypt(GET_SCORE);
					clientConnection.sendMessage(b);
					}
					});

			distancesRefreshButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
					// TODO: send a get distances message.
					String c = s + cipher.encrypt(LIST_DISTANCES);
					clientConnection.sendMessage(c);

					}
					});

			final EditText distancesTextView = (EditText) findViewById(R.id.distancesTextMultiline);
			distancesTextView.setEnabled(false);

			escortRefreshButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
					// TODO: send a get requests message.
					String d = s + cipher.encrypt(LIST_REQUESTS);
					clientConnection.sendMessage(d);

					}
					});

			moveSubmit.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
					String selectedLocation = (String) locationsSpinner
					.getSelectedItem();
					// TODO: send a move request.
					String e = "MOVE(" + selectedLocation + ")";
					clientConnection.sendMessage(e);
					}
					});

			// TODO: create a View.OnClickListener for the escortSubmit and set it
			// accordingly (similar to the previous ones)
			escortSubmit.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
					String selectedLocation = (String) escortsSpinner
					.getSelectedItem();
					// TODO: send a move request.
					String e = "ESCORT(" + selectedRequest.substring(0, 4) + ")";
					clientConnection.sendMessage(e);
					}
					});

			mHandler = new Handler() {
				@Override
					public void handleMessage(android.os.Message msg) {
						String message = (String) msg.obj;
						if (message.startsWith("CURRENT_LOCATION:")) {
							locationTextView.setText(message.substring(message
										.indexOf(":") + 1));
						} else if (message.startsWith("PENDING_REQUEST:")) {
							displayRequests(message.substring(message.indexOf(":") + 1));
						} else if (message.startsWith("MOVE:DONE")) {
							new AlertDialog.Builder(safeWalkApp).setTitle("Move done.")
								.setMessage("The move request was successful")
								.show();
						} else if(message.startsWith("SCORE:")) {
							scoreTextView.setText(message.substring(message.indexOf(":")+1));
						} else if(message.startsWith("MOVE:ACK")) {
							new AlertDialog.Builder(safeWalkApp).setTitle("Move ack.")
								.setMessage("The move ack was successful")
								.show();
						} else if(message.startsWith("MOVE:REJECT")) {
							new AlertDialog.Builder(safeWalkApp).setTitle("Move reject.")
								.setMessage("The move reject was successful")
								.show();
						} else if(message.startsWith("ESCORT:ACK")) {
							new AlertDialog.Builder(safeWalkApp).setTitle("Escort ack.")
								.setMessage("The escort ack was successful")
								.show();
						} else if(message.startsWith("ESCORT:REJECT")) {
							new AlertDialog.Builder(safeWalkApp).setTitle("Escort reject.")
								.setMessage("The escort reject was successful")
								.show();
						} else if(message.startsWith("ESCORT:DONE")) {
							new AlertDialog.Builder(safeWalkApp).setTitle("Escort done.")
								.setMessage("The escort request was successful")
								.show();
						} else if(message.startWith("DISTANCES")) {
					String tt = message;
					String rr = tt.substring(tt.indexOf(":")+1);
					ArrayList<String> locationsList = new ArrayList<String>();
					String[] mm =  rr.split(",");
					int locAIndex, locBIndex;
					String locA, locB, dis;
					for(int i = 0; i<mm.length; i++) {
						locA = mm[i].substring(0, mm[i].indexOf("<"));
						locB = mm[i].substring(mm[i].indexOf(">")+1, mm[i].indexOf("="));
						dis = mm[i].substring(mm[i].indexOf("="));
						if(!locationsList.contains(locA))
							locationsList.add(locA);
						if(!locationsList.contains(locB))
							locationsList.add(locB);
					}
					int[][] distancesArray = new int[locationsList.size()][locationsList.size()];
					for(int i = 0; i<locationsList.size(); i++) {
						locAIndex = locationsList.indexOf(locA);
						locBIndex = locationsList.indexOf(locB);
						int disInt = Integer.parseInt(dis);
						distancesArray[locAIndex][locBIndex]=disInt;
						distancesArray[locBIndex][locAIndex]=disInt;
				     }
					 StringBuilder distancesString = new StringBuilder();
					 distancesString.append("    ");
					 for(String s : locationsList) {
						 String sf = String.format("%6s", s);
						 distancesString.append(sf);
					 }
					 distancesString.append("\n");
				     for(int i = 0; i<locationList.size(); i++) {
					     distancesString.append(locationsList(i));
						 for(int y = 0; y<distancesArray[i]; i++) {
					         String d = String.format("%6d", distancesArray[i][y]);
							 distancesString.append(d);
						 }
						 distancesString.append("\n");
					}
					ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(safeWalkApp, android.R.layout.simple_spinner_dropdown_item, locationsList);
					locationsSpinner.setAdapter(spinnerArrayAdapter);
					distancesTextView.setText(distancesString.toString());
               }
			}

			private void displayRequests(String message) {
				String[] segments = message.split(",");
				ArrayList<String> requests = new ArrayList<String>();
				for (String segment : segments) {
					requests.add(segment);
					Log.w("Request", segment);
				}
				ArrayAdapter<String> requestsArrayAdapter = new ArrayAdapter<String>(
						safeWalkApp,
						android.R.layout.simple_spinner_dropdown_item, requests);
				escortsSpinner.setAdapter(requestsArrayAdapter);
			}
		};
}

/**
 * messageReceived handler. The message will be received on a different
 * thread, therefore we need to use an android message handler to pass the
 * message content to the main thread.
 */
public void messageReceived(String message) {
	Log.w("SafeWalkMessage", message);
	android.os.Message msg = new android.os.Message();
	msg.obj = message;
	mHandler.sendMessage(msg);
}
}
