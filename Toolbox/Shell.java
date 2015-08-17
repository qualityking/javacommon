package com.automation.toolbox.protocol.ssh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class Shell {
	static java.util.Properties config = new java.util.Properties();
	private Session session = null;
	private String host = "";
	private int port = 0;
	private String username = "";
	private String Password = "";

	public Shell(String host, String username, String password) {
		this.host = host;
		this.username = username;
		this.Password = password;
		config.put("StrictHostKeyChecking", "no");
	}

	public Shell(String host, int port, String username, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.Password = password;
		config.put("StrictHostKeyChecking", "no");
	}

	public boolean connect() {
		JSch shell = new JSch();
		try {
			if (port == 0) {
				session = shell.getSession(username, host);
			} else {
				session = shell.getSession(username, host, port);
			}
			session.setPassword(Password);
			session.setConfig(config);
			session.connect();
			if (session.isConnected()) {
				return true;
			}
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean isConnected() {
		if (session == null) {
			return false;
		}
		try {
			if (session.isConnected()) {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean disconnect() {
		try {
			session.disconnect();
			if (!session.isConnected()) {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static String RunCommand(String host, String username, String password, String command) {
		return RunCommand(host, 0, username, password, command);
	}

	public static String RunCommand(String host, int port, String username, String password, String command) {
		Shell shell = null;
		if (port > 0) {
			shell = new Shell(host, port, username, password);
		} else {
			shell = new Shell(host, username, password);
		}
		shell.connect();
		return shell.RunCommand(command);
	}

	public String RunCommand(String command) {
		if (!isConnected()) {
			try {
				throw new Exception("Shell RunCommand Exception : Session is not connected, please call connect method");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.err.println(e.getMessage());
				return "";
			}
		}
		String output = "";
		try {
			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			InputStream inputStream = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (inputStream.available() > 0) {
					int i = inputStream.read(tmp, 0, 1024);
					if (i < 0)
						break;
					String _output = new String(tmp, 0, i);
					output = output + _output;
				}
				if (channel.isClosed()) {
					if (inputStream.available() > 0)
						continue;
					// System.out.println("exit-status: "+channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			return output;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}

	// to be implemented
	public static void uploadFile(String host, String username, String password, String sourceFilePath, String destinationPath) {
		uploadFile(host, 0, username, password, sourceFilePath, destinationPath);
	}

	public static void uploadFile(String host,  int port, String username, String password, String sourceFilePath, String destinationPath) {
		Shell shell = null;
		if (port > 0) {
			shell = new Shell(host, port, username, password);
		} else {
			shell = new Shell(host, username, password);
		}
		
		shell.uploadFile(sourceFilePath,destinationPath);
		
		
	}
	
	
	
	
	public void uploadFile(String sourceFilePath, String destinationPath) {
		FileInputStream fis = null;
		try {
			String user = this.username;
			String host = this.host;
			String pass = this.Password;
			String rfile = destinationPath;
			String lfile = sourceFilePath;

			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, 22);
			session.setPassword(pass);
			session.setConfig(config);
			session.connect();
			boolean ptimestamp = true;
			// exec 'scp -t rfile' remotely
			String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + rfile;
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();
			channel.connect();
			if (checkAck(in) != 0) {
				//System.exit(0);
				return; 
			}
			File _lfile = new File(lfile);
			if (ptimestamp) {
				command = "T " + (_lfile.lastModified() / 1000) + " 0";
				// The access time should be sent here,
				// but it is not accessible with JavaAPI ;-<
				command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
				out.write(command.getBytes());
				out.flush();
				if (checkAck(in) != 0) {
					//System.exit(0);
					return; 
				}
			}
			// send "C0644 filesize filename", where filename should not include
			// '/'
			long filesize = _lfile.length();
			command = "C0644 " + filesize + " ";
			if (lfile.lastIndexOf('/') > 0) {
				command += lfile.substring(lfile.lastIndexOf('/') + 1);
			} else {
				command += lfile;
			}
			command += "\n";
			out.write(command.getBytes());
			out.flush();
			if (checkAck(in) != 0) {
				//System.exit(0);
				return; 
			}
			// send a content of lfile
			fis = new FileInputStream(lfile);
			byte[] buf = new byte[1024];
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				out.write(buf, 0, len); // out.flush();
			}
			fis.close();
			fis = null;
			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			if (checkAck(in) != 0) {
				//System.exit(0);
				return; 
			}
			out.close();
			channel.disconnect();
			session.disconnect();
			//System.exit(0);
			return; 
		} catch (Exception e) {
			System.out.println(e);
			try {
				if (fis != null)
					fis.close();
			} catch (Exception ee) {
			}
		}
	}

	public static void downloadFile(String host, String username, String password, String sourceFilePath, String destinationPath) {
		uploadFile(host, 0, username, password, sourceFilePath, destinationPath);
	}

	public static void downloadFile(String host,  int port, String username, String password, String sourceFilePath, String destinationPath) {
		Shell shell = null;
		if (port > 0) {
			shell = new Shell(host, port, username, password);
		} else {
			shell = new Shell(host, username, password);
		}
		
		shell.downloadFile(sourceFilePath,destinationPath);
		
		
	}
	
	public void downloadFile(String sourceFilePath, String destinationPath) {
		FileOutputStream fos = null;
		try {
			String user = this.username;
			String host = this.host;
			String pass = this.Password;
			String rfile = sourceFilePath;
			String lfile = destinationPath;
			String prefix = null;
			if (new File(lfile).isDirectory()) {
				prefix = lfile + File.separator;
			}
			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, 22);
			session.setPassword(pass);
			session.setConfig(config);
			session.connect();
			// exec 'scp -f rfile' remotely
			String command = "scp -f " + rfile;
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] buf = new byte[1024];
			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			while (true) {
				int c = checkAck(in);
				if (c != 'C') {
					break;
				}
				// read '0644 '
				in.read(buf, 0, 5);
				long filesize = 0L;
				while (true) {
					if (in.read(buf, 0, 1) < 0) {
						// error
						break;
					}
					if (buf[0] == ' ')
						break;
					filesize = filesize * 10L + (long) (buf[0] - '0');
				}
				String file = null;
				for (int i = 0;; i++) {
					in.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a) {
						file = new String(buf, 0, i);
						break;
					}
				}
				// System.out.println("filesize="+filesize+", file="+file);
				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
				// read a content of lfile
				fos = new FileOutputStream(prefix == null ? lfile : prefix + file);
				int foo;
				while (true) {
					if (buf.length < filesize) {
						foo = buf.length;
					} else {
						foo = (int) filesize;
					}
					foo = in.read(buf, 0, foo);
					if (foo < 0) {
						// error
						break;
					}
					fos.write(buf, 0, foo);
					filesize -= foo;
					if (filesize == 0L)
						break;
				}
				fos.close();
				fos = null;
				if (checkAck(in) != 0) {
					//System.exit(0);
					return;
				}
				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
			}
			session.disconnect();
			//System.exit(0);
			return; 
		} catch (Exception e) {
			System.out.println(e);
			try {
				if (fos != null)
					fos.close();
			} catch (Exception ee) {
			}
		}
	}

	static int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0)
			return b;
		if (b == -1)
			return b;
		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				System.out.print(sb.toString());
			}
			if (b == 2) { // fatal error
				System.out.print(sb.toString());
			}
		}
		return b;
	}
}
