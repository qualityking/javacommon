using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;

namespace Framework
{
    public class Mouse
    {
        //HwndSource hwnd = (HwndSource)HwndSource.FromVisual(ctr);
        //AutomationElement elm = AutomationElement.FromHandle(hwnd.Handle);
        private dynamic ctr;

        public Mouse(dynamic ctrl)
        {
            this.ctr = ctrl;
        }

        public void Click()
        {
            Application.Current.Dispatcher.Invoke(new Action(() =>
              {
                  if (ctr is Control)
                  {
                      Control ctrl = (Control)ctr;
                      ctrl.RaiseEvent(new RoutedEventArgs(Button.ClickEvent));
                  }
                  else
                  {
                      int w = (int)ctr.ActualWidth / 2;
                      int h = (int)ctr.ActualHeight / 2;

                      Point relativePoint = ctr.PointToScreen(new Point(w, h));
                      MouseOperations.SetCursorPosition((int)relativePoint.X, (int)relativePoint.Y);
                      MouseOperations.MouseEvent(MouseOperations.MouseEventFlags.LeftDown);
                      MouseOperations.MouseEvent(MouseOperations.MouseEventFlags.LeftUp);
                  }
              }));
        }

        public void DoubleClick()
        {
            Application.Current.Dispatcher.Invoke(new Action(() =>
            {
                if (ctr is Control)
                {
                    Control ctrl = (Control)ctr;
                    ctrl.RaiseEvent(new RoutedEventArgs(Button.MouseDoubleClickEvent));
                }
                else
                {
                    int w = (int)ctr.ActualWidth / 2;
                    int h = (int)ctr.ActualHeight / 2;

                    Point relativePoint = ctr.PointToScreen(new Point(w, h));
                    MouseOperations.SetCursorPosition((int)relativePoint.X, (int)relativePoint.Y);
                    MouseOperations.MouseEvent(MouseOperations.MouseEventFlags.LeftDown);
                    MouseOperations.MouseEvent(MouseOperations.MouseEventFlags.LeftUp);
                    MouseOperations.MouseEvent(MouseOperations.MouseEventFlags.LeftDown);
                    MouseOperations.MouseEvent(MouseOperations.MouseEventFlags.LeftUp);
                }

            }));
        }

        public void RightClick()
        {
            Application.Current.Dispatcher.Invoke(new Action(() =>
            {
                if (ctr is Control)
                {
                    Control ctrl = (Control)ctr;
                    ctrl.RaiseEvent(new RoutedEventArgs(Button.MouseRightButtonDownEvent));
                    ctrl.RaiseEvent(new RoutedEventArgs(Button.MouseRightButtonUpEvent));
                }
                else
                {
                    int w = (int)ctr.ActualWidth / 2;
                    int h = (int)ctr.ActualHeight / 2;

                    Point relativePoint = ctr.PointToScreen(new Point(w, h));
                    MouseOperations.SetCursorPosition((int)relativePoint.X, (int)relativePoint.Y);
                    MouseOperations.MouseEvent(MouseOperations.MouseEventFlags.RightDown);
                    MouseOperations.MouseEvent(MouseOperations.MouseEventFlags.RightUp);
                }

            }));
        }
    }
}
