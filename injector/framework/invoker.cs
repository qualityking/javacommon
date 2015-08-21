using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading;
using System.Windows;

namespace Framework
{
   public class Invoker
    {
       private object obj; 
       
       public Invoker(object obj) {
           this.obj = obj; 
       }

       public bool WaitProperty(string propertyName, object value, int TimeOutInSec) { 
           String val = null;

           String pVal = ("" + value).ToLower();

           while (TimeOutInSec-- > 0 && val != value)
           {
               val = "" + GetProperty(propertyName);
               if (val.ToLower().Equals(pVal))
               {
                   return true; 
               }
               Thread.Sleep(1000); 
           }
           return false; 
       }

       public dynamic GetProperty(string propertyName){
            try
            {
            object value = null;
            Application.Current.Dispatcher.Invoke(new Action(() =>
            {
                Type T = obj.GetType();
                IEnumerable<MemberInfo> allProps = T.GetMembers().Where(p => p.MemberType == MemberTypes.Property && p.Name == propertyName);
                if (allProps.Count() > 0) { 
                    PropertyInfo prop = (PropertyInfo) allProps.First();
                    value = prop.GetValue(obj, null);
                }
            }));

            return value;
            }
            catch (Exception)
            {
                return null; 
            }
        }

       public T GetProperty<T>(string propertyName)  where T : TestObject
        {
            try
            {
                object value = null;
                Application.Current.Dispatcher.Invoke(new Action(() =>
                {
                    Type type = obj.GetType();
                    IEnumerable<MemberInfo> allProps = type.GetMembers().Where(p => p.MemberType == MemberTypes.Property && p.Name == propertyName);
                    if (allProps.Count() > 0)
                    {
                        PropertyInfo prop = (PropertyInfo)allProps.First();
                        value = prop.GetValue(obj, null);
                    }
                }));

                return (T)Activator.CreateInstance(typeof(T), new Object[] { value });
            }
            catch (Exception)
            {
                return null;
            }
        }

       public void SetProperty(string propertyName, object value)
        {
            try
            {
                Application.Current.Dispatcher.Invoke(new Action(() =>
                {
                    Type T = obj.GetType();
                    IEnumerable<MemberInfo> allProps = T.GetMembers().Where(p => p.MemberType == MemberTypes.Property && p.Name == propertyName);
                    if (allProps.Count() > 0)
                    {
                        PropertyInfo prop = (PropertyInfo)allProps.First();
                        prop.SetValue(obj, value,null);
                    }
                }));

            }
            catch (Exception)
            {
               
            }
        }

       public object CallMethod(string MethodName)
        {
            return CallMethod(MethodName, null);
        }

       public object CallMethod( string MethodName, params object[] parameters)
        {
            object retObject = null;

            try
            {
                Application.Current.Dispatcher.Invoke(new Action(() =>
                {
                    Type T = obj.GetType();
                    IEnumerable<MemberInfo> allProps = T.GetMembers().Where(p => p.MemberType == MemberTypes.Method && p.Name == MethodName);
                    if (allProps.Count() > 0)
                    {

                        MethodInfo method = (MethodInfo)allProps.First();
                        retObject = method.Invoke(obj, parameters);
                    }
                }));

                return retObject;
            }
            catch (Exception)
            {
                return retObject;
            }
        }
 
    }
}
