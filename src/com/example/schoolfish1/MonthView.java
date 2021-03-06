package com.example.schoolfish1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

//use Joda Time Library?

public class MonthView extends Activity implements OnClickListener
{
	private static final String tag = "SchoolFish MonthView";

	private Button currentMonth;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridView weekTabView; 
	private GridCellAdapter adapter;
	private WeekTabAdapter tabAdapter; 
	private Calendar calendar;
	private int month, year;
	private final DateFormat dateFormatter = new DateFormat();
	private static final String dateTemplate = "MMMM yyyy";
	private int actualCurrentMonth; 
	private int actualCurrentYear; 
	private GridView weekdayHeaderView; 
	private int numWeeks; //number of weeks in this month -- number of week tabs needed
	private String[] weeks; //array of each week's tab 
	private ArrayList<Integer> firstDaysOfWeeks = new ArrayList<Integer>(6);  

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_month_view);

			calendar = Calendar.getInstance();
			month = calendar.get(Calendar.MONTH) + 1;
			actualCurrentMonth = calendar.get(Calendar.MONTH);
			actualCurrentYear = calendar.get(Calendar.YEAR);
			year = calendar.get(Calendar.YEAR);
			Log.d(tag, "Calendar Instance:= Day: " + calendar.get(Calendar.DATE) +  " Month: " + month + " " + "Year: " + year);

			prevMonth = (ImageView) this.findViewById(R.id.prevMonth);
			prevMonth.setOnClickListener(this);

			currentMonth = (Button) this.findViewById(R.id.currentMonth);
			currentMonth.setText(dateFormatter.format(dateTemplate, calendar.getTime()));

			nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
			nextMonth.setOnClickListener(this);

			//Set up Weekday Header Bar
			final String[] weekdays = new String[] {"Mon", "Tue", "Wed", "Thu", "Fri"}; 
			weekdayHeaderView = (GridView) findViewById(R.id.calendarHeader);
			ArrayAdapter<String> weekdayAdapter = new ArrayAdapter<String>(this, R.layout.calendar_header_cell, weekdays);
			weekdayHeaderView.setAdapter(weekdayAdapter); 
			
			calendarView = (GridView) this.findViewById(R.id.monthCalendar);
			adapter = new GridCellAdapter(getApplicationContext(), R.id.calendar_day_gridcell, month, year);
			adapter.notifyDataSetChanged();
			calendarView.setAdapter(adapter);
			
			weekTabView = (GridView) this.findViewById(R.id.weekTabs); 
			tabAdapter = new WeekTabAdapter(getApplicationContext(), R.id.week_tab); 
			tabAdapter.notifyDataSetChanged(); 
			weekTabView.setAdapter(tabAdapter); 	

		}

	/**
	 * 
	 * @param month
	 * @param year
	 */
	private void setGridCellAdapterToDate(int month, int year)
		{
			adapter = new GridCellAdapter(getApplicationContext(), R.id.calendar_day_gridcell, month, year);
			calendar.set(year, month - 1, calendar.get(Calendar.DAY_OF_MONTH));
			currentMonth.setText(dateFormatter.format(dateTemplate, calendar.getTime()));
			adapter.notifyDataSetChanged();
			calendarView.setAdapter(adapter);
		}

	@Override
	public void onClick(View v)
		{
			if (v == prevMonth)
				{
					if (month <= 1)
						{
							month = 12;
							year--;
						}
					else
						{
							month--;
						}
					Log.d(tag, "Setting Prev Month in GridCellAdapter: " + "Month: " + month + " Year: " + year);
					setGridCellAdapterToDate(month, year);
				}
			if (v == nextMonth)
				{
					if (month > 11)
						{
							month = 1;
							year++;
						}
					else
						{
							month++;
						}
					Log.d(tag, "Setting Next Month in GridCellAdapter: " + "Month: " + month + " Year: " + year);
					setGridCellAdapterToDate(month, year);
				}

		}

	@Override
	public void onDestroy()
		{
			Log.d(tag, "Destroying View ...");
			super.onDestroy();
		}

	//Inner Class -- need to figure out how to generate as many GridView rows as there are numWeeks in a month, and
	//place 1 weekTab per week 
	public class WeekTabAdapter extends BaseAdapter implements OnClickListener{
		private final Context _context;
		private Button weekTab; 

		//Constructor
		public WeekTabAdapter(Context applicationContext, int weekTabView) {
			this._context = applicationContext;
			weeks = new String[numWeeks]; //initialise String array of week tabs 
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			
			if (v == null)
				{
					LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = inflater.inflate(R.layout.week_tab, parent, false);
				}

			// Get a reference to the Day gridcell
			weekTab = (Button) v.findViewById(R.id.week_tab);
			weekTab.setOnClickListener(this);
			
			// Set the Day GridCell
			weekTab.setText("");
			weekTab.setTag(position); 

			return v;
		}

		@Override
		public void onClick(View v) {
			int weekNum = (Integer) v.getTag();
			int firstDayOfWeek = firstDaysOfWeeks.get(weekNum);
			
			Bundle b = new Bundle(); //create new bundle to pass info via intent into new activity
		    b.putInt("year", year); //store year in bundle
		    b.putInt("month", month); //store month in bundle
		    b.putInt("firstDayOfWeek", firstDayOfWeek); 

		    Intent newActivity = new Intent("WeekView");  //create new intent to start WeekView Activity   
		    
			Log.d(tag, "Parsed Week Number: " + weekNum);
			Log.d(tag, "This Year: " + year); 
			Log.d(tag, "This Month: " + month); 
			Log.d(tag, "First Date of week: " + firstDayOfWeek); 
			//if user clicks on week tab, start new activity of that week's calendar 
				
				//put bundle in Intent and start new activity with intent 
			    newActivity.putExtras(b); //Put your id to your next Intent
        		startActivity(newActivity); //start activity with intent 
			
		}//end onClick()
		
		public int getCount() {
			return numWeeks;
		}

		public Object getItem(int position) {
			return weeks[position];
		}

		public long getItemId(int position) {
			return position;
		}
		
	}

	// Inner Class
	public class GridCellAdapter extends BaseAdapter implements OnClickListener
		{
			private static final String tag = "GridCellAdapter";
			private final Context _context;

			private final List<String> list;
			private static final int DAY_OFFSET = 1;
			private final String[] weekdays = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
			private final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
			private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
			private final int month, year;
			private int daysInMonth, prevMonthDays;
			private int currentDayOfMonth;
			private int currentWeekDay;
			private Button gridcell;
			private TextView num_events_per_day;
			private final HashMap eventsPerMonthMap;
			private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");

			// Days in Current Month
			public GridCellAdapter(Context context, int textViewResourceId, int month, int year)
				{
					super();
					this._context = context;
					this.list = new ArrayList<String>();
					this.month = month;
					this.year = year;

					Log.d(tag, "==> Passed in Date FOR Month: " + month + " " + "Year: " + year);
					Calendar calendar = Calendar.getInstance();
					currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
					currentWeekDay = calendar.get(Calendar.DAY_OF_WEEK);
					Log.d(tag, "New Calendar:= " + calendar.getTime().toString());
					Log.d(tag, "CurrentDayOfWeek :" + currentWeekDay);
					Log.d(tag, "CurrentDayOfMonth :" + currentDayOfMonth);

					// Print Month
					printMonth(month, year);

					// Find Number of Events
					eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
				}
			private String getMonthAsString(int i)
				{
					return months[i];
				}
			
			private int getMonthAsInt(String s){
				int month = 0; 
				
				if(s.equals("January")){
					month = 0; 
				}else if(s.equals("February")){
					month = 1; 
				}else if(s.equals("March")){
					month = 2; 
				}else if(s.equals("April")){
					month = 3; 
				}else if(s.equals("May")){
					month = 4;
				}else if(s.equals("June")){
					month = 5; 
				}else if(s.equals("July")){
					month = 6; 
				}else if(s.equals("August")){
					month = 7; 
				}else if(s.equals("September")){
					month = 8; 
				}else if(s.equals("October")){
					month = 9; 
				}else if(s.equals("November")){
					month = 10; 
				}else if(s.equals("December")){
					month = 11; 
				}
				
				return month; 
				
			}

			private String getWeekDayAsString(int i)
				{
					return weekdays[i];
				}

			private int getNumberOfDaysOfMonth(int i)
				{
					return daysOfMonth[i];
				}

			public String getItem(int position)
				{
					return list.get(position);
				}

			@Override
			public int getCount()
				{
					return list.size();
				}

			/**
			 * Prints Month
			 * 
			 * @param mm
			 * @param yy
			 */
			private void printMonth(int mm, int yy)
				{
					Log.d(tag, "==> printMonth: mm: " + mm + " " + "yy: " + yy);
					// The number of days to leave blank at
					// the start of this month.
					int trailingSpaces = 0;
					int daysInPrevMonth = 0;
					int prevMonth = 0;
					int prevYear = 0;
					int nextMonth = 0;
					int nextYear = 0;
					

					int currentMonth = mm - 1;
					int currentYear = yy; 
					String currentMonthName = getMonthAsString(currentMonth);
					daysInMonth = getNumberOfDaysOfMonth(currentMonth);

					Log.d(tag, "Current Month: " + " " + currentMonthName + " having " + daysInMonth + " days.");

					// Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
					GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
					Log.d(tag, "Gregorian Calendar:= " + cal.getTime().toString());

					if (currentMonth == 11)
						{
							prevMonth = currentMonth - 1;
							daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
							nextMonth = 0;
							prevYear = yy;
							nextYear = yy + 1;
							Log.d(tag, "*->PrevYear: " + prevYear + " PrevMonth:" + prevMonth + " NextMonth: " + nextMonth + " NextYear: " + nextYear);
						}
					else if (currentMonth == 0)
						{
							prevMonth = 11;
							prevYear = yy - 1;
							nextYear = yy;
							daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
							nextMonth = 1;
							Log.d(tag, "**--> PrevYear: " + prevYear + " PrevMonth:" + prevMonth + " NextMonth: " + nextMonth + " NextYear: " + nextYear);
						}
					else
						{
							prevMonth = currentMonth - 1;
							nextMonth = currentMonth + 1;
							nextYear = yy;
							prevYear = yy;
							daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
							Log.d(tag, "***---> PrevYear: " + prevYear + " PrevMonth:" + prevMonth + " NextMonth: " + nextMonth + " NextYear: " + nextYear);
						}

					// Compute how much to leave before before the first day of the month.
					// getDay() returns 0 for Sunday.
					int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
					//if start of month is a Sun or Mon, there are no trailing spaces. Else, there are
					//as many trailing spaces as the currentWeekDay index - 1
					if(currentWeekDay == 0 || currentWeekDay == 6){//if month starts on Sat or Sun, no trailing spaces and first weekday is not the 1st
						trailingSpaces = 0; 
					}
					else{
						trailingSpaces = currentWeekDay - 1; 
					}
				

					Log.d(tag, "Week Day:" + currentWeekDay + " is " + getWeekDayAsString(currentWeekDay));
					Log.d(tag, "No. Trailing space to Add: " + trailingSpaces);
					Log.d(tag, "No. of Days in Previous Month: " + daysInPrevMonth);

					if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1)
						{
							++daysInMonth;
						}

					// Trailing Days from Last Month -- grey out as inactive
					for (int i = 0; i < trailingSpaces; i++)
						{
							Log.d(tag, "PREV MONTH:= " + prevMonth + " => " + getMonthAsString(prevMonth) + " " + String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i));
							list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i) + "-GREY" + "-" + getMonthAsString(prevMonth) + "-" + prevYear);
							if(i == 0){
								firstDaysOfWeeks.add((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i); //if the first day of the month is NOT Monday, the first week will start with the previous month 
							}
						}
					
					int thisWeekDay = currentWeekDay; 
		
					for (int i = 1; i <= daysInMonth; i++)
						{
						//if that day is not a Saturday or Sunday, then add to printed list 
							if((((thisWeekDay) % 7) != 0) && ((((thisWeekDay) + 1) % 7) != 0)){
								Log.d(currentMonthName, String.valueOf(i) + " " + getMonthAsString(currentMonth) + " " + yy);
								if (i == currentDayOfMonth && currentMonth == actualCurrentMonth && currentYear == actualCurrentYear)
									{
										list.add(String.valueOf(i) + "-ORANGE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
									}
								else
									{
										list.add(String.valueOf(i) + "-BLACK" + "-" + getMonthAsString(currentMonth) + "-" + yy);
									}
							if((thisWeekDay % 7) == 1){
								//if date is Monday, add to firstDaysOfWeek array
								 firstDaysOfWeeks.add(i); 
								 Log.d(tag, "Adding " + i + " to firstDaysOfWeeks"); 
							}
						}
						thisWeekDay++; 
						}//end for
						
					// Leading Days from Next Month -- grey out as inactive
					for (int i = 0; i < list.size() % 5; i++)
						{
							Log.d(tag, "NEXT MONTH:= " + getMonthAsString(nextMonth));
							list.add(String.valueOf(i + 1) + "-GREY" + "-" + getMonthAsString(nextMonth) + "-" + nextYear);
						}
					
					//Get the number of weeks displayed in a month
					numWeeks = list.size()/5; 
					Log.d(tag, "THIS MONTH HAS " + numWeeks + " WEEKS IN IT"); 
				}

			/**
			 * NOTE: YOU NEED TO IMPLEMENT THIS PART Given the YEAR, MONTH, retrieve
			 * ALL entries from a SQLite database for that month. Iterate over the
			 * List of All entries, and get the dateCreated, which is converted into
			 * day.
			 * 
			 * @param year
			 * @param month
			 * @return
			 */
			private HashMap findNumberOfEventsPerMonth(int year, int month)
				{
					HashMap map = new HashMap<String, Integer>();
					// DateFormat dateFormatter2 = new DateFormat();
					//						
					// String day = dateFormatter2.format("dd", dateCreated).toString();
					//
					// if (map.containsKey(day))
					// {
					// Integer val = (Integer) map.get(day) + 1;
					// map.put(day, val);
					// }
					// else
					// {
					// map.put(day, 1);
					// }
					return map;
				}

			@Override
			public long getItemId(int position)
				{
					return position;
				}

			@Override
			public View getView(int position, View convertView, ViewGroup parent)
				{
					View row = convertView;
					if (row == null)
						{
							LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
							row = inflater.inflate(R.layout.calendar_day_gridcell, parent, false);
						}

					// Get a reference to the Day gridcell
					gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
					gridcell.setOnClickListener(this);

					// ACCOUNT FOR SPACING

					Log.d(tag, "Current Day: " + currentDayOfMonth);
					String[] day_color = list.get(position).split("-");
					String theday = day_color[0];
					String themonth = day_color[2];
					String theyear = day_color[3];
					if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null))
						{
							if (eventsPerMonthMap.containsKey(theday))
								{
									num_events_per_day = (TextView) row.findViewById(R.id.num_events_per_day);
									Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
									num_events_per_day.setText(numEvents.toString());
								}
						}

					// Set the Day GridCell
					gridcell.setText(theday);
					gridcell.setTag(theday + "-" + themonth + "-" + theyear);
					Log.d(tag, "Setting GridCell " + theday + "-" + themonth + "-" + theyear);

					if (day_color[1].equals("GREY"))
						{
							gridcell.setTextColor(Color.LTGRAY);
						}
					if (day_color[1].equals("BLACK"))
						{
							gridcell.setTextColor(Color.BLACK);
						}
					if (day_color[1].equals("ORANGE"))
						{
							gridcell.setTextColor(getResources().getColor(R.color.highlight_text_color));
						}
					return row;
				}
			
			@Override
			public void onClick(View view)
				{
					String date_month_year = (String) view.getTag();
					
					String[] dateArray = date_month_year.split("-");
					
					Bundle b = new Bundle(); //create new bundle to pass info via intent into new activity
		        	b.putInt("date", Integer.parseInt(dateArray[0])); 
		        	b.putInt("month", getMonthAsInt(dateArray[1])); 
		        	b.putInt("year", Integer.parseInt(dateArray[2])); 
					Intent i = new Intent("DayView");
					i.putExtras(b); 
		            startActivity(i); 

					try
						{
							Date parsedDate = dateFormatter.parse(date_month_year);
							Log.d(tag, "Parsed Date: " + parsedDate.toString());

						}
					catch (ParseException e)
						{
							e.printStackTrace();
						}
				}

		}


	
}//end MonthView class
