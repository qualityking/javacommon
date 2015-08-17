using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace DialogHandler
{
    class Program
    {
        const int BM_CLICK = 0x00F5;
        const int WM_CLOSE = 0x0010;
        const int WM_LBUTTONDOWN = 0x0201;
        const int WM_LBUTTONUP = 0x0202;
        const int BM_SETSTATE = 0x00F3;


        [DllImport("user32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        public static extern int GetWindowText(IntPtr hWnd, StringBuilder lpString, int nMaxCount);

        [DllImport("user32.dll")]
        [return: MarshalAs(UnmanagedType.Bool)]
        private static extern bool EnumWindows(EnumWindowsProc lpEnumFunc, ref SearchData data);

        [DllImport("user32.dll", SetLastError = true)]
        public static extern uint GetWindowThreadProcessId(IntPtr hWnd, [Out] uint processId);


        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        public static extern int GetClassName(IntPtr hWnd, StringBuilder lpClassName, int nMaxCount);

        public delegate bool EnumWindowsProc(IntPtr hWnd, ref SearchData data);

        [DllImport("user32.dll")]
        [return: MarshalAs(UnmanagedType.Bool)]
        static extern bool EnumChildWindows(IntPtr hwndParent, EnumWindowsProc lpEnumFunc, ref SearchData data);


        [DllImport("user32.dll")]
        static extern IntPtr SendMessage(IntPtr hWnd, int msg, IntPtr wParam, IntPtr lParam);

        [DllImport("user32.dll", CharSet = CharSet.Auto, ExactSpelling = true)]
        public static extern bool EnumThreadWindows(int dwThreadId, EnumWindowsProc lpEnumFunc, ref SearchData data);


        [DllImport("user32.dll", SetLastError = true)]
        static extern IntPtr FindWindowEx(IntPtr hwndParent, IntPtr hwndChildAfter, string lpszClass, String lpszWindow);

        [DllImport("user32.dll", ExactSpelling = true, CharSet = CharSet.Auto)]
        public static extern IntPtr GetParent(IntPtr hWnd);

        static void Main(string[] args)
        {
            //args[0] = "(.*Oracle.*)|(.*raframework.*)";

            IntPtr dialog = findPointer("#32770", "Windows Internet Explorer", IntPtr.Zero);
            IntPtr btnSave = findPointer("Button", "&Save", dialog);

            //SendMessage(btnSave, BM_CLICK, IntPtr.Zero, IntPtr.Zero); //this function is causing IE crash
            // therefore using alternative to simulate mouse click
            SendMessage(btnSave, WM_LBUTTONDOWN, IntPtr.Zero, IntPtr.Zero);
            System.Threading.Thread.Sleep(200);
            SendMessage(btnSave, WM_LBUTTONUP, IntPtr.Zero, IntPtr.Zero);

            System.Threading.Thread.Sleep(1000);

            IntPtr mainwindowHandle = getRunningHwnd("iexplore", args[0]);
            IntPtr Iframe = FindWindowEx(mainwindowHandle, IntPtr.Zero, "IEFrame", null);

            IntPtr NotificationBar = FindWindowEx(mainwindowHandle, IntPtr.Zero, "Frame Notification Bar", null);
            SendMessage(NotificationBar, WM_CLOSE, IntPtr.Zero, IntPtr.Zero);
        }

        public static Process getRunningProcess(String ProcessName, String Title)
        {
            Process[] pname = Process.GetProcessesByName(ProcessName);
            foreach (Process p in pname)
            {
                var sbWindowText = new StringBuilder(1024);
                GetWindowText(p.MainWindowHandle, sbWindowText, sbWindowText.Capacity);
                if (Title == null || Regex.IsMatch(sbWindowText.ToString(), Title))
                {
                    return p;
                }
            }
            return null;
        }


        public static IntPtr getRunningHwnd(String ProcessName, String Title)
        {
            Process[] pname = Process.GetProcessesByName(ProcessName);
            foreach (Process p in pname)
            {
                var sbWindowText = new StringBuilder(1024);
                GetWindowText(p.MainWindowHandle, sbWindowText, sbWindowText.Capacity);
                if (Title == null || Regex.IsMatch(sbWindowText.ToString(), Title))
                {
                    return p.MainWindowHandle;
                }

            }
            return new IntPtr(0);
        }

        public static IntPtr findPointer(String strClassName, String strTitle, IntPtr Parent)
        {
            var searchData = new SearchData
            {
                ClassName = strClassName,
                Title = strTitle
            };

            EnumChildWindows(Parent, EnumProc, ref searchData);

            if (searchData.Result.Count > 0)
            {
                return searchData.Result[0];
            }
            else
            {
                return IntPtr.Zero;
            }

        }


        public static bool EnumProc(IntPtr hWnd, ref SearchData searchData)
        {
            var sbClassName = new StringBuilder(1024);
            GetClassName(hWnd, sbClassName, sbClassName.Capacity);
            if (searchData.ClassName == null || Regex.IsMatch(sbClassName.ToString(), searchData.ClassName))
            {
                var sbWindowText = new StringBuilder(1024);
                GetWindowText(hWnd, sbWindowText, sbWindowText.Capacity);
                if (searchData.Title == null || Regex.IsMatch(sbWindowText.ToString(), searchData.Title))
                {
                    searchData.Result.Add(hWnd);
                }
            }
            return true;
        }

    }

    public class SearchData
    {
        public string ClassName { get; set; }
        public string Title { get; set; }

        private readonly List<IntPtr> _result = new List<IntPtr>();
        public List<IntPtr> Result
        {
            get { return _result; }
        }
    }
}
