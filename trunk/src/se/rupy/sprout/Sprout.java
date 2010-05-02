package se.rupy.sprout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import se.rupy.http.Event;
import se.rupy.http.Service;
import se.rupy.memory.Base;
import se.rupy.memory.LinkBean;
import se.rupy.pool.Connection;
import se.rupy.pool.Pool;
import se.rupy.pool.Settings;
import se.rupy.util.Log;

public abstract class Sprout extends Service implements Type {
	public static String ROOT = "app/content";
	private static Base DB;
	private static Properties I18N;
	private static boolean TRANSLATE;

	static {
		DB = new Base();

		try {
			MySQL mysql = new MySQL();
			DB.init(new Pool(mysql, mysql));
		}
		catch(SQLException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(ROOT + File.separator + "i18n.txt"), "UTF-8"));
			String line = in.readLine();
			I18N = new Properties();

			while(line != null) {
				int equals = line.indexOf("=");

				if(equals > -1) {
					String name = line.substring(0, equals).trim();
					String value = line.substring(equals + 1).trim();

					int comment = value.indexOf("//");

					if(comment > 0) {
						value = value.substring(0, comment).trim();
					}

					I18N.put(name, value);
				}

				line = in.readLine();
			}

			in.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		TRANSLATE = I18N.getProperty("translate", null) != null;
	}

	public static String i18n(String text) {
		if(translate()) {
			return I18N.getProperty(text, text);
		}
		return text;
	}

	public static boolean translate() {
		return TRANSLATE;
	}
	
	public static String language() {
		return I18N.getProperty("language", "general");
	}

	public static String clean(String line) {
		line = line.replaceAll("&", "&amp;"); // leave first
		line = line.replaceAll("\"", "&quot;");
		line = line.replaceAll("'", "&apos;");
		line = line.replaceAll("<", "&lt;");
		line = line.replaceAll(">", "&gt;");
		return line;
	}

	public static Data generate(short type, int length) throws Exception {
		Data data = new Data();
		data.setType(type);
		data.setValue(Event.random(length));

		while(update(Base.SELECT, data)) {
			data.setValue(Event.random(length));
		}

		return data;
	}

	public static boolean update(byte type, Object o) throws SQLException {
		return update(type, o, null);
	}

	public static boolean update(byte type, Object o, Connection connection) throws SQLException {
		if(connection == null) {
			return DB.update(type, o);
		}
		else {
			return DB.update(type, o, connection);
		}
	}

	public static LinkedList from(String name, String sql) throws SQLException {
		return DB.query(Base.FROM, name, sql);
	}

	public static LinkedList where(String name, String sql) throws SQLException {
		return DB.query(Base.WHERE, name, sql);
	}

	public static long value(String sql) throws SQLException {
		return DB.value(sql);
	}

	public static void find(String sql) throws SQLException {
		DB.find(sql, connection(false), true);
	}

	public static void find(String sql, Connection connection) throws SQLException {
		DB.find(sql, connection, true);
	}

	public static Connection connection(boolean transaction) throws SQLException {
		return DB.connection(transaction);
	}

	public static void redirect(Event event) throws IOException, Event {
		String referer = event.query().header("referer");
		redirect(event, referer == null ? "/" : referer, true);
	}

	public static void redirect(Event event, String path) throws IOException, Event {
		redirect(event, path, false);
	}

	public static void redirect(Event event, String path, boolean forward) throws IOException, Event {
		if(forward) {
			HashMap query = (HashMap) event.query().clone();
			event.session().put("post", query);
		}

		event.reply().header("Location", path);
		event.reply().code("302 Found");
		throw event;
	}

	public static class Cache extends LinkBean {
		public boolean invalid = true;

		public Cache(boolean invalid) {
			this.invalid = invalid;
		}
	}

	public static class MySQL implements Log, Settings {
		private PrintStream out;

		public MySQL() {
			try {
				out = new PrintStream(new FileOutputStream("log.txt", true));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public String driver() {
			return System.getProperty("dbdriver", "com.mysql.jdbc.Driver");
		}

		public String url() {
			return System.getProperty("dburl", "jdbc:mysql://localhost/sprout");
		}

		public String user() {
			return System.getProperty("dbuser", "root");
		}

		public String pass() {
			return System.getProperty("dbpass", "");
		}

		public void message(Object o) {
			if(out == null) {
				if(o instanceof Throwable) {
					((Throwable) o).printStackTrace();
				} else {
					System.out.println(new SimpleDateFormat("yy/MM/dd HH:mm:ss")
					.format(Calendar.getInstance().getTime())
					+ " " + o);
				}
			} else {
				if(o instanceof Throwable) {
					((Throwable) o).printStackTrace(out);
				} else if(o instanceof java.sql.Statement) {
					String s = o.toString();
					int index = s.indexOf(':');
					
					if(index > 0) {
						s = s.substring(index + 1, s.length()).trim();
					}
					
					out.println(new SimpleDateFormat("yy/MM/dd HH:mm:ss")
					.format(Calendar.getInstance().getTime())
					+ " " + s);
				} else {
					out.println(new SimpleDateFormat("yy/MM/dd HH:mm:ss")
					.format(Calendar.getInstance().getTime())
					+ " " + o);
				}
			}
		}
	}
}
