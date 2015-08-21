using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Media.Media3D;
using System.Windows.Shapes;


namespace Framework
{
  public class Finder 
    {
      List<DependencyObject> ObjectList = new List<DependencyObject>();
      public ArrayList excludeClass = null; 
      public int ImplicitWaitInSec = 0;
      private dynamic testObj =null;

      public Finder()
      {
          excludeClass = new ArrayList();
          excludeClass.Add("System.Windows.Controls.Grid");
          excludeClass.Add("System.Windows.Shapes");
      }

      public Finder(int ImplicitWaitInSec=0) {
          excludeClass = new ArrayList();
          excludeClass.Add("System.Windows.Controls.Grid");
          excludeClass.Add("System.Windows.Shapes");
          this.ImplicitWaitInSec = ImplicitWaitInSec; 
      }

      public Finder(object testObject, int ImplicitWaitInSec = 0)
      {
          excludeClass = new ArrayList();
          excludeClass.Add("System.Windows.Controls.Grid");
          excludeClass.Add("System.Windows.Shapes");
          this.testObj = testObject;
          this.ImplicitWaitInSec = ImplicitWaitInSec; 
      }

      public TestObject Parent(String ParentClrFullClassName, Dictionary<String, object> ParentPropValuePair = null)
      {
          Invoker invoker = new Invoker(testObj);
          object Parent = invoker.GetProperty("Parent");
          if (Parent == null) {
              return null; 
          }

          while (Parent !=null)
          {
              if (Parent.GetType().ToString().Equals(ParentClrFullClassName))
              {
                  if (ParentPropValuePair == null)
                  {
                      return new TestObject(Parent);
                  }
                  else
                  {

                      if (IsPropertyValueMatches(Parent, ParentPropValuePair))
                      {
                          return new TestObject(Parent);
                      }
                      else {
                          Parent = new Invoker(Parent).GetProperty("Parent");
                      }

                  }
              }
              else {
                  Parent = new Invoker(Parent).GetProperty("Parent");
              }
          }

          return null; 

      }

      private bool IsPropertyValueMatches(dynamic parent, Dictionary<String, object> PropValuePair)
      {
          Invoker invoker = new Invoker(parent);
          foreach (String key in PropValuePair.Keys) {
              object val = invoker.GetProperty(key);
              if (val == null) {
                  return false;
              }
              if (!PropValuePair[key].Equals("" + val)) {
                  return false; 
              }
          }
          return true; 
      }

      public TestObject Find(String clrFullClassName, String PropName, Object PropValue, int Index = 0, int TimeoutInSeconds= -1)
      {
          Dictionary<String, object> PropValuePair = new Dictionary<string, object>();
          PropValuePair.Add(PropName, PropValue);
          if (TimeoutInSeconds >= 0)
          {
              return Find(clrFullClassName, TimeoutInSeconds, 1, PropValuePair, Index);
          }
          else
          {
              return Find(clrFullClassName, ImplicitWaitInSec, 1, PropValuePair, Index);
          }
      }

      public TestObject Find(String clrFullClassName, Dictionary<String, object> PropValuePair = null, int Index = 0)
      {
          return Find(clrFullClassName, ImplicitWaitInSec, 1, PropValuePair, Index);
      }

      public TestObject Find(String clrFullClassName, int TimeoutInSeconds, int IntervelInSec = 1, Dictionary<String, object> PropValuePair = null, int Index = 0)
      {
          try
          {
              dynamic obj = null;
              //Application.Current.Dispatcher.Invoke(new Action(() =>
              //{
              //    obj = getObject(clrFullClassName, PropValuePair, Index);
              //}));
              //if (obj != null)
              //{
              //    return new TestObject(obj);
              //}

              DateTime dt = DateTime.Now;
              while ((DateTime.Now - dt).Seconds < TimeoutInSeconds)
              {
                  Application.Current.Dispatcher.Invoke(new Action(() =>
                  {
                      obj = getObject(clrFullClassName, PropValuePair, Index);
                  }));
                  if (obj != null)
                  {
                      return new TestObject(obj);
                  }

                  Thread.Sleep(IntervelInSec * 1000);
              }
              return null;
          }

          catch (Exception ex)
          {
              Utility.WriteToLog(ex.Message); 
              return null; 
          }
      }

      public static TestObject getWindowObject(String WindowClrClassName, String Title = "") {
          TestObject window = null;
          foreach (Window win in Application.Current.Windows)
          {
              if (Title.Equals(""))
              {
                  if (win.IsVisible == true && !win.GetType().ToString().Equals(WindowClrClassName))
                  {
                      window = new TestObject(win);
                  }
              }
              else
              {
                  if (win.IsVisible == true && !win.GetType().ToString().Equals(WindowClrClassName) && win.Title == Title)
                  {
                      window = new TestObject(win);
                  }
              }
          }
          return window;
      }

      private dynamic getObject(String clrFullClassName, Dictionary<String, object> PropValuePair = null, int Index = 0)
        {
            ArrayList lst = new ArrayList();

          
            if (testObj == null)
            {
                GetControlTree(Application.Current.MainWindow);
            }
            else
            {
                GetControlTree(testObj);
            }

            foreach (object ctrl in ObjectList)
            {

                if (ctrl.GetType().ToString().Equals(clrFullClassName))
                {

                    if (PropValuePair == null && Index == 0)
                    {
                        dynamic dynamicCtrl = ctrl;
                        return dynamicCtrl;
                    }
                    else if (PropValuePair == null && Index > 0)
                    {
                        dynamic dynamicCtrl = ctrl;
                        lst.Add(dynamicCtrl);
                    }
                    else if (PropValuePair != null)
                    {

                        Type T = ctrl.GetType();
                        bool flgMatch = true;
                        IEnumerable<MemberInfo> allProps = T.GetMembers().Where(p => p.MemberType == MemberTypes.Property);

                        foreach (string propname in PropValuePair.Keys)
                        {
                            IEnumerable<MemberInfo> currentProp = allProps.Where(p => p.Name == propname);
                            if (currentProp.Count() > 0)
                            {
                                PropertyInfo prop = (PropertyInfo)currentProp.First();
                                if (!prop.GetValue(ctrl, null).Equals(PropValuePair[prop.Name]))
                                {
                                    flgMatch = false;
                                    break;
                                }
                            }
                            else
                            {
                                flgMatch = false;
                                break;
                            }
                        }


                        if (flgMatch)
                        {
                            dynamic dynamicCtrl = ctrl;
                            lst.Add(dynamicCtrl);
                        }

                    }
                }
            }


            if (lst.Count > Index)
            {
                return lst[Index];
            }

            return null;
        }
     
      /*
      private  IEnumerable<T> FindVisualChildren<T>(DependencyObject depObj) where T : DependencyObject
        {
            if (depObj != null)
            {
                for (int i = 0; i < VisualTreeHelper.GetChildrenCount(depObj); i++)
                {
                    DependencyObject child = VisualTreeHelper.GetChild(depObj, i);
                    if (child != null && child is T)
                    {
                        yield return (T)child;
                    }

                    foreach (T childOfChild in FindVisualChildren<T>(child))
                    {
                        yield return childOfChild;
                    }
                }
            }
        }

        private IEnumerable FindLogicalChildren(dynamic depObj) 
        {
            if (depObj != null)
            {
                if (depObj is DependencyObject || depObj is FrameworkElement || depObj is FrameworkContentElement)
                {
                    foreach (object child in LogicalTreeHelper.GetChildren(depObj))
                    {
                        if (child != null)
                        {
                            yield return child;
                        }

                        foreach (object childOfChild in FindLogicalChildren((dynamic)child))
                        {
                            yield return childOfChild;
                        }
                    }

                }
            }
        }
      */

        public void GetControlTree(object objNode) //XElement parent)
        {
            if (objNode != null && objNode is DependencyObject)
            {
                DependencyObject Node = (DependencyObject)objNode;
                ObjectList.Add(Node);

                if (Node is Visual || Node is Visual3D)
                {
                    if (VisualTreeHelper.GetChildrenCount(Node) > 0)        //Node has Childrens
                    {
                        
                        for (int i = 0; i < VisualTreeHelper.GetChildrenCount(Node); i++)
                        {
                            try
                            {
                                DependencyObject ChildNode = VisualTreeHelper.GetChild(Node, i);
                                GetControlTree(ChildNode);
                            }
                            catch{
                            }
                        }
                    }
                    else
                    {
                        ObjectList.Add(Node);
                    }
                }
                else
                {
                    foreach (object ChildNode in LogicalTreeHelper.GetChildren(Node))
                    {
                        if (ChildNode is DependencyObject)
                        {
                            ObjectList.Add((DependencyObject)ChildNode);
                            GetControlTree(ChildNode);
                        }
                        
                    }

                }

            }
        }

        public void printControlTree() {
            ObjectList.Clear();
            Application.Current.Dispatcher.Invoke(new Action(() =>
            {
                GetControlTree(Application.Current.MainWindow);
            }));
            
            foreach (var item in ObjectList)
            {
                Utility.WriteToLog(item.GetType().ToString());
            }
        }
  
        public void PrintAllPropertyValue(String clrFullClassName, int Index = 0)
        {
            dynamic ctrl = Find(clrFullClassName, null, Index);
            Type T = ctrl.GetType();

            IEnumerable<MemberInfo> allProps = T.GetMembers().Where(p => p.MemberType == MemberTypes.Property);
            foreach (MemberInfo mi in allProps)
            {

                PropertyInfo prop = (PropertyInfo)mi;
                Utility.WriteToLog(prop.Name + " = " + prop.GetValue(ctrl, null)); 
                
            }
        }

        //public void PrintAllLogicalElement()
        //{
        //    foreach (object ctrl in FindLogicalChildren(Application.Current.MainWindow))
        //    {
        //        bool flag = true; 
        //        if (ctrl is UIElement)
        //        {
        //            string className = ctrl.GetType().ToString();
        //            foreach (string str in excludeClass)
        //            {
        //                if (className.Contains(str))
        //                {
        //                    flag = false;
        //                    break;
        //                }
        //            }
        //            if (flag)
        //            {
        //                Utility.WriteToLog(ctrl.GetType().ToString());
        //            }
                    
        //        }
        //    }
        //}     
    }
}
