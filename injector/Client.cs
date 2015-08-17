using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading;
using System.Windows;
using System.IO;
using System.Windows.Threading;
namespace TestServer
{
    class ClientApplication 
    {
        MethodInfo minfo;
        dynamic instance;
        public static Thread TestRunnerThread;
        Assembly Framework;
        EventWaitHandle handle = new EventWaitHandle(false, EventResetMode.ManualReset, "MyWaitHandle");

        public void LoadTestCaseAssembilies(String [] param)
        {
            String path = param[0];

            String dir = Path.GetDirectoryName(path);
            foreach (string dll in Directory.GetFiles(dir,"*.dll"))
            {
                if (dll != path) {
                   Framework = Assembly.Load(File.ReadAllBytes(dll));
                }    
            }
            Assembly assembly = Assembly.Load(File.ReadAllBytes(path));

            Type type = assembly.GetType(param[1]);
            instance = Activator.CreateInstance(type,handle);
            minfo = type.GetMethods().Where(s => s.Name.Equals(param[2])).First();
            AppDomain currentDomain = AppDomain.CurrentDomain;
            currentDomain.AssemblyResolve += new ResolveEventHandler(MyResolveEventHandler);


            SnoopApplication();
        }

        private Assembly MyResolveEventHandler(object sender, ResolveEventArgs args)
        {
            return Framework;
        }

        public void SnoopApplication()
        {
            Dispatcher dispatcher;
            if (Application.Current == null)
                dispatcher = Dispatcher.CurrentDispatcher;
            else
                dispatcher = Application.Current.Dispatcher;

            if (dispatcher.CheckAccess())
            {
                handle.Reset();
                ThreadStart ts = (ThreadStart)Delegate.CreateDelegate(typeof(ThreadStart), instance, minfo, true);
                Thread UIthread = new Thread(ts);
                UIthread.SetApartmentState(ApartmentState.STA);
                UIthread.Start();

          //     minfo.Invoke(instance, null); 
            }
            else
            {
                dispatcher.Invoke((Action)SnoopApplication);
                return;
            }
        }

      

        public static void Init(String AssemblyLocation, String ClassName, String MethodName )
        {
            
            String[] param = { AssemblyLocation, ClassName, MethodName };
            ThreadStart starter = delegate { new ClientApplication().LoadTestCaseAssembilies(param); };
            TestRunnerThread = new Thread(starter);
            TestRunnerThread.Name = "SnoopThread";
            TestRunnerThread.Start();
        }

        
    }
}
