using System;
using System.Collections.Generic;

using System.Windows.Forms;
using QuickFix;
using System.Threading;
using System.Text;
using System.IO;


namespace FixInitiator
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        //[STAThread]
        public static void Main(string[] args)
        {
            if (args.Length != 3 && args.Length != 2)
            {
                Console.WriteLine("USAGE : FixInitiator.exe <ConfigFilePath> <MessageFilePath/MassageString> <TagsDelimiter>");
                Console.Read();
                return;
            }
            if (RunTestCase(args))
            {
                Console.WriteLine("PASS");
                //Console.Read();
            }
            else
            {
                Console.WriteLine("FAIL");
            }
        }

        static bool RunTestCase(string[] args)
        {
            string configFile = "";
            string MessageFile = "";
            string TagsDelimiter = "";
            if (args.Length == 3)
            {
                 configFile = args[0]; //@"C:\ManishBansal\code\Fix code\FixInitiator\clsafixbuy.cfg"
                 MessageFile = args[1];
                 TagsDelimiter = args[2];
            }
            else if (args.Length == 2)
            {
                 configFile = args[0]; //@"C:\ManishBansal\code\Fix code\FixInitiator\clsafixbuy.cfg"
                 MessageFile = args[1];
            }
            String[] messages = null;
            try
            {
                if (File.Exists(MessageFile))
                {
                    messages = File.ReadAllLines(MessageFile);
                }
                else
                {

                    MessageFile = MessageFile.Split('=')[1];
                    MessageFile = MessageFile.Replace(">", "=");
                    TagsDelimiter = "☺";
                    //MessageFile = MessageFile.Replace(">", "=");
                    messages = new String[] { MessageFile };
                }
                messageSender(messages, configFile, TagsDelimiter);
                return true;
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return false;
            }
        }

        static void WriteOutputData(string OutputData)
        {
            string filePath = System.IO.Path.GetTempPath() + "\\output.txt";
            if (File.Exists(filePath))
            {
                File.Delete(filePath);
            }
            File.WriteAllText(filePath, OutputData + "\n\n");
        }

        static void messageSender(String[] messages, string configFile, string TagsDelimiter)
        {
            SocketInitiator initiator = StartSocketInitiator(configFile);
            SessionID sessionID = StartFixSession(initiator);
            string[] allDealInst = new string[messages.Length];
            for (int i = 0; i < messages.Length;i++)
            {
               allDealInst[i]=sendFixMessage(sessionID, TagsDelimiter, messages[i]);
            }
            WriteOutputData(String.Join("\n", allDealInst));
            //Console.ReadLine();
            initiator.stop();
        }

        static SocketInitiator StartSocketInitiator(String configFile)
        {
            ClientInitiator app = new ClientInitiator();
            SessionSettings settings = new SessionSettings(configFile);
            QuickFix.Application application = new ClientInitiator();
            FileStoreFactory storeFactory = new FileStoreFactory(settings);
            ScreenLogFactory logFactory = new ScreenLogFactory(settings);
            MessageFactory messageFactory = new DefaultMessageFactory();

            SocketInitiator initiator = new SocketInitiator(application, storeFactory, settings, logFactory, messageFactory);
            initiator.start();
            Thread.Sleep(3000);
            return initiator;

        }

        static SessionID StartFixSession(SocketInitiator initiator)
        {
            System.Collections.ArrayList list = initiator.getSessions();
            SessionID sessionID = (SessionID)list[0];
            Session.lookupSession(sessionID).logon();

            while (!Session.lookupSession(sessionID).isLoggedOn())
            {
                Thread.Sleep(1000);
            }
            return sessionID;
        }

        static string sendFixMessage(SessionID sessionID, String TagsDelimiter, String message)
        {
            DateTime newtime = DateTime.UtcNow;
            string ClientNotes = "DealInst" + newtime.ToString("yyMMddHHmmss");
            
            FixMessageBuilder messagebuilder = new FixMessageBuilder(TagsDelimiter);
            message = messagebuilder.removeTag(message, new string[] { "8", "9", "10", "11", "58", "52", "60" });
            message = messagebuilder.addSendingTime(message);
            message = messagebuilder.addTradingTime(message);
            message = messagebuilder.addClOrderId(message);
            message = messagebuilder.addClientNotes(message, ClientNotes);
            message = message.Replace(TagsDelimiter, "\u0001");
            messagebuilder = new FixMessageBuilder("\u0001");
            message = messagebuilder.AddBodyLength(message);
            message = messagebuilder.addBeginString(message);
            message = messagebuilder.AddSum(message);

            QuickFix.Message newMsg = new QuickFix.Message(message);

            //Console.WriteLine("Sending Order to Server : " + newMsg.ToString());
            Session.sendToTarget(newMsg, sessionID);
            return ClientNotes;
        }

    }

    public class ClientInitiator : QuickFix.Application
    {

        public void onCreate(QuickFix.SessionID value)
        {
            //Console.WriteLine("Message OnCreate" + value.toString());
        }

        public void onLogon(QuickFix.SessionID value)
        {
           // Console.WriteLine("OnLogon" + value.toString());
        }

        public void onLogout(QuickFix.SessionID value)
        {
          //   Console.WriteLine("Log out Session" + value.toString());
        }

        public void toAdmin(QuickFix.Message value, QuickFix.SessionID session)
        {
          //  Console.WriteLine("Called Admin :" + value.ToString());
        }

        public void toApp(QuickFix.Message value, QuickFix.SessionID session)
        {
            //  Console.WriteLine("Called toApp :" + value.ToString());
        }

        public void fromAdmin(QuickFix.Message value, SessionID session)
        {
            // Console.WriteLine("Got message from Admin" + value.ToString());
        }

        public void fromApp(QuickFix.Message value, SessionID session)
        {
            //if (value is QuickFix42.ExecutionReport)
            //{
            //    QuickFix42.ExecutionReport er = (QuickFix42.ExecutionReport)value;
            //    ExecType et = (ExecType)er.getExecType();
            //    if (et.getValue() == ExecType.FILL)
            //    {
            //        //TODO: implement code
            //    }
            //}

            //Console.WriteLine("Got Execution Report from Server \n" + value.ToString());
        }
    }
}
