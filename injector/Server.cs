using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace TestServer
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class Server : Window
    {
        public Server()
        {
            InitializeComponent();
          
        }

        EventWaitHandle handle = new EventWaitHandle(false, EventResetMode.ManualReset,  "MyWaitHandle");
        Thread enableBtn = null; 
        private void btn1_Click(object sender, RoutedEventArgs e)
        {

            Int32 processid = System.Diagnostics.Process.GetProcessesByName("Cobra").First().Id;
            string Location = typeof(ClientApplication).Assembly.Location;

            String AssemblyName = @"C:\IT_QA\VS2013_workspace\TestServer\Framework\bin\Debug\Framework.dll";
            String ClassFullName = "Framework.Driver";
            String MethodName = "Run";

            String param = AssemblyName + "," + ClassFullName + "," + MethodName;
            ManagedInjector.Injector.Launch(processid, Location, "TestServer.ClientApplication", "Init", param);

            this.btn1.IsEnabled = false;
            enableBtn = new Thread(enableRunButton);
            enableBtn.Start(); 

        }

        private void btn1_Spy_Click(object sender, RoutedEventArgs e)
        {
            Int32 processid = System.Diagnostics.Process.GetProcessesByName("Cobra").First().Id;
            string Location = typeof(ClientApplication).Assembly.Location;

            //String AssemblyName = @"C:\IT_QA\VS2013_workspace\TestServer\TestCode\bin\Debug\TestCode.dll";
            //String ClassFullName = "TestCode.Driver";
            //String MethodName = "Run";

            String AssemblyName = @"C:\IT_QA\VS2013_workspace\TestServer\NewSpy\bin\Debug\NewSpy.exe";
            String ClassFullName = "NewSpy.MainClass";
            String MethodName = "CreateInstance";


            String param = AssemblyName + "," + ClassFullName + "," + MethodName;
            ManagedInjector.Injector.Launch(processid, Location, "TestServer.ClientApplication", "Spy", param);
           
        }

        private void enableRunButton() {
            handle.WaitOne();
            handle.Reset();

            Application.Current.Dispatcher.Invoke(new Action(() =>
                {
                    btn1.IsEnabled = true; 

                }));
        }


       
    }
}
