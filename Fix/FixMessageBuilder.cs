using System;
using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;

namespace FixInitiator
{
    class FixMessageBuilder
    {
        private string delimiter;

        public FixMessageBuilder(string TagsDelimiter)
        {
            this.delimiter = TagsDelimiter; 
        }

        public  string Build(string message)
        {
            //message = message.Replace(delimiter, "\u0001");
            //message = AddBodyLength(message);
            //message = addBeginString(message);
            return AddSum(message);
        }

        public  string addSendingTime(string message, string time = "")
        {
            if (!message.Contains(delimiter + "52="))
            {
                DateTime newtime;
                if (time == "")
                {
                    newtime = DateTime.UtcNow;
                    time = newtime.ToString("yyyyMMdd-HH:mm:ss.fff");
                }

                message = RegExReplace(message, @"\" + delimiter + "52=.*?" + @"\" + delimiter, delimiter);
                return message + "52=" + time + delimiter;
            }
            else
            {
                return message;
            }
        }
        
        public string addTradingTime(string message, string time = "")
        {
            if (!message.Contains(delimiter + "60="))
            {
                DateTime newtime;
                if (time == "")
                {
                    newtime = DateTime.UtcNow;
                    time = newtime.ToString("yyyyMMdd-HH:mm:ss.fff");
                }
                message = RegExReplace(message, @"\" + delimiter + "60=.*?" + @"\" + delimiter, delimiter);
                return message + "60=" + time + delimiter;
            }
            else
            {
                return message;
            }
        }

        public string addClOrderId(string message, string ClOrdID = "")
        {
                System.Threading.Thread.Sleep(1);
                DateTime newtime;
                if (ClOrdID == "")
                {
                    newtime = DateTime.UtcNow;
                    ClOrdID = newtime.ToString("yyMMddHHmmssfff");
                }
                message = RegExReplace(message, @"\" + delimiter + "11=.*?" + @"\" + delimiter, delimiter);
                return message + "11=" + ClOrdID + delimiter;
        }

        public string addClientNotes(string message, string ClientNotes = "")
        {
            System.Threading.Thread.Sleep(1);
            DateTime newtime;
            if (ClientNotes == "")
            {
                newtime = DateTime.UtcNow;
                ClientNotes = "DealInst" + newtime.ToString("yyMMddHHmmssfff");
            }
            message = RegExReplace(message, @"\" + delimiter + "58=.*?" + @"\" + delimiter, delimiter);
            return message + "58=" + ClientNotes + delimiter;
        }

        public string AddBodyLength(string message)
        {
            string msgLength = delimiter + "9=";
            if (message.IndexOf(msgLength) < 0)
            {
                return "9=" + message.Length + delimiter + message;
            }
            else
            {
                message = RegExReplace(message, @"\" + delimiter + "9=.*?" + @"\" + delimiter, delimiter);
                return "9=" + message.Length + delimiter + message;
            }

        }

        public string addBeginString(String message)
        {
            if (!message.Contains("8=FIX.4.2"))
            {
                return "8=FIX.4.2" + delimiter + message;
            }
            else
            {
                return message;
            }
        }

        public string AddSum(string message)
        {
            //message = message.Replace(delimiter, "\u0001");
            char[] inputChars = message.ToCharArray();
            int checkSum = 0;
            foreach (char aChar in inputChars)
            {
                checkSum += aChar;
            }
            int SUM = (checkSum % 256);
            return message + "10=" + SUM + '\u0001';
        }

        private string RegExReplace(string str, string pattern, string replacement){
            Regex rgx = new Regex(pattern);
            return rgx.Replace(str, replacement);

        }

        public string removeTag(string message,string[] tagNumber)
        {
            foreach (var item in tagNumber)
            {
                if (item == "8")
                {
                    if (message.IndexOf(item + "=") == 0)
                    {
                        //message = RegExReplace(message, item + "=.*?" + @"\" + delimiter, delimiter);
                        int firstIndexOfDelimeter = message.IndexOf(delimiter);
                        message = message.Substring(firstIndexOfDelimeter);
                    }
                }
                else
                {
                    if (message.IndexOf(delimiter + item + "=") >= 0)
                    {
                        message = RegExReplace(message, @"\" + delimiter + item + "=.*?" + @"\" + delimiter, delimiter);
                    }
                }
            }
            message = message.Substring(delimiter.Length);
            return message;
        }
    }
}
