import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import com.briup.environment.bean.Environment;
import com.briup.environment.server.DBStore;
import com.briup.environment.server.DBStoreImpl;

public class DBStoreTest {

	public static void main(String[] args) {

		try {
			DBStore dbStore = new DBStoreImpl();
			Collection<Environment> coll = new ArrayList<Environment>();
			String day = "2018-03-19";
			Calendar c = Calendar.getInstance();
			Date date1 = new SimpleDateFormat("yy-MM-dd").parse(day);
			c.setTime(date1);
	        int day1 = c.get(Calendar.DATE);
			Long date = new Date().getTime();
			Long time = Long.valueOf(date);
			Environment e1 = new Environment("温度", "2", "3", "4", "5", 6, "7", 8, 9, new Timestamp(time));
			Environment e2 = new Environment("温度", "2", "3", "4", "5", 6, "7", 8, 9, new Timestamp(time));
			Environment e3 = new Environment("温度", "2", "3", "4", "5", 6, "7", 8, 9, new Timestamp(time));
			Environment e8 = new Environment("温度", "2", "3", "4", "5", 6, "7", 8, 9, new Timestamp(day1));
			Environment e9 = new Environment("温度", "2", "3", "4", "5", 6, "7", 8, 9, new Timestamp(day1));
			Environment e10 = new Environment("温度", "2", "3", "4", "5", 6, "7", 8, 9, new Timestamp(day1));
			Environment e4 = new Environment("温度", "2", "3", "4", "5", 6, "7", 8, 9, new Timestamp(time));
			Environment e5 = new Environment("温度", "2", "3", "4", "5", 6, "7", 8, 9, new Timestamp(time));
			Environment e6 = new Environment("温度", "2", "3", "4", "5", 6, "7", 8, 9, new Timestamp(time));
			Environment e7 = new Environment("温度", "2", "3", "4", "5", 6, "7", 8, 9, new Timestamp(time));
			
			coll.add(e1);
			coll.add(e2);
			coll.add(e3);
			coll.add(e4);
			coll.add(e5);
			coll.add(e6);
			coll.add(e7);
			coll.add(e8);
			coll.add(e9);
			coll.add(e10);
			dbStore.saveDb(coll);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
