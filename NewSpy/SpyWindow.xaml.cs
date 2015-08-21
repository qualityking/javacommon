using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
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
using System.Data;
using System.Reflection;
using System.Threading;
using System.Collections;
using System.Windows.Media.Media3D;
using System.Runtime.InteropServices; 

namespace NewSpy
{  
    public partial class SpyWindow : Window
    {
        ArrayList excludeClass;

        Dictionary<String, DependencyObject> allObjests = new Dictionary<String, DependencyObject>();

        List<ObjectProperties> propval = null;

        public SpyWindow()
        {
            InitializeComponent();
            excludeClass = new ArrayList();
            excludeClass.Add("System.Windows.Controls.Grid");
            excludeClass.Add("System.Windows.Shapes");
        }

       

        public void GetCompleteTree(object objNode, TreeViewItem parent)
        {
            if (objNode != null && objNode is DependencyObject)
            {
                DependencyObject Node = (DependencyObject)objNode;

                if (Node is Visual || Node is Visual3D)
                {
                    if (VisualTreeHelper.GetChildrenCount(Node) > 0)        //Node has Childrens
                    {

                        for (int i = 0; i < VisualTreeHelper.GetChildrenCount(Node); i++)
                        {

                            if (Node is Control || Node.GetType().ToString().Equals("System.Windows.Controls.TextBlock"))
                            {
                                TreeViewItem currentNode = new TreeViewItem();
                                currentNode.MouseDoubleClick += TreeViewItem_PreviewMouseDoubleClick;
                                currentNode.Header = Node.GetType();
                                currentNode.Name = "OBJ" + allObjests.Count + 1;
                                allObjests.Add(currentNode.Name, Node);
                                parent.Items.Add(currentNode);
                                GetCompleteTree(VisualTreeHelper.GetChild(Node, i), currentNode);
                            }
                            else
                            {
                                GetVisualTree(VisualTreeHelper.GetChild(Node, i), parent);
                            }

                        }
                    }
                    else
                    {
                        //Leaf Element
                        if (Node is Control || Node.GetType().ToString().Equals("System.Windows.Controls.TextBlock"))
                        {
                            
                            TreeViewItem currentNode = new TreeViewItem();
                            currentNode.MouseDoubleClick += TreeViewItem_PreviewMouseDoubleClick;
                            currentNode.Header = Node.GetType();
                            currentNode.Name = "OBJ" + allObjests.Count + 1;
                            allObjests.Add(currentNode.Name, Node);
                            parent.Items.Add(currentNode);
                        }
                    }
                }
                else
                {

                    foreach (object ChildNode in LogicalTreeHelper.GetChildren(Node))
                    {
                        if (ChildNode is DependencyObject)
                        {
                            TreeViewItem currentNode = new TreeViewItem();
                            currentNode.MouseDoubleClick += TreeViewItem_PreviewMouseDoubleClick;
                            currentNode.Header = Node.GetType();
                            currentNode.Name = "OBJ" + allObjests.Count + 1;
                            allObjests.Add(currentNode.Name, Node);
                            parent.Items.Add(currentNode);
                            GetCompleteTree(ChildNode, currentNode);
                        }
                    }
                }
            }
        }

        public void GetVisualTree(DependencyObject Node, TreeViewItem parent)
        {
            if (Node != null)
            {
                if (VisualTreeHelper.GetChildrenCount(Node) > 0)        //Node has Childrens
                {

                    for (int i = 0; i < VisualTreeHelper.GetChildrenCount(Node); i++)
                    {

                        if (Node is Control || Node.GetType().ToString().Equals("System.Windows.Controls.TextBlock"))
                        {
                            TreeViewItem currentNode = new TreeViewItem();
                            currentNode.MouseDoubleClick += TreeViewItem_PreviewMouseDoubleClick;

                            currentNode.Header = Node.GetType();
                            currentNode.Name = "OBJ" + allObjests.Count + 1;
                            allObjests.Add(currentNode.Name, Node);
                            parent.Items.Add(currentNode);
                            GetVisualTree(VisualTreeHelper.GetChild(Node, i), currentNode);
                        }
                        else {
                            GetVisualTree(VisualTreeHelper.GetChild(Node, i), parent);
                        }
                        
                    }
                }
                else
                {
                    //Leaf Element
                    if (Node is Control || Node.GetType().ToString().Equals("System.Windows.Controls.TextBlock"))
                    {
                        TreeViewItem currentNode = new TreeViewItem();
                        currentNode.MouseDoubleClick+= TreeViewItem_PreviewMouseDoubleClick;
                        currentNode.Header = Node.GetType();
                        currentNode.Name = "OBJ" + allObjests.Count + 1;
                        allObjests.Add(currentNode.Name, Node);
                        parent.Items.Add(currentNode);
                    }
                }
            }
        }

        public DependencyObject FindMainGrid(DependencyObject Node)
        {
            if (Node != null)
            {
                if (VisualTreeHelper.GetChildrenCount(Node) > 0)        //Node has Childrens
                {
                    for (int i = 0; i < VisualTreeHelper.GetChildrenCount(Node); i++)
                    {
                        if (Node.GetType().ToString().Equals("System.Windows.Controls.Grid"))
                        {
                            return Node;
                        }
                        else
                        {
                            return FindMainGrid(VisualTreeHelper.GetChild(Node, i));
                        }
                    }
                }
                else
                {
                    if (Node.GetType().ToString().Equals("System.Windows.Controls.Grid"))
                    {
                        return Node;
                    }
                }
            }
            return null;
        }

        private void Window_Loaded_1(object sender, RoutedEventArgs e)
        {
            LoadObjectTree();
        }

        private void LoadObjectTree()
        {
            try
            {
                TreeViewItem rootNode = new TreeViewItem();
                rootNode.Header = "Application";
                Application.Current.Dispatcher.Invoke(new Action(() =>
                {
                    foreach (Window win in Application.Current.Windows)
                    {
                       // if (win.IsVisible == true)
                        if (win.IsVisible == true && win.GetType() != typeof(NewSpy.SpyWindow))
                        {
                            DependencyObject rootdObj = win;
                            GetCompleteTree(rootdObj, rootNode);
                        }
                    }
                }));
                treeVisual.Items.Add(rootNode);
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
        }

        private string createTreePath(TreeViewItem tNode)
        {
            List<string> lstTreeNodes = new List<string>();
            //StringBuilder sb = new StringBuilder("");
            if (tNode != null)
            {
                do
                {
                    lstTreeNodes.Add(tNode.Header.ToString());
                    tNode = tNode.Parent as TreeViewItem;
                } while (!tNode.Header.ToString().Equals("Application"));
            }
            lstTreeNodes.Reverse();
            string[] myArr = lstTreeNodes.ToArray<string>();
            return String.Join("==>", myArr);
            //return sb.ToString();
        }

        private void TreeViewItem_PreviewMouseDoubleClick(object sender, RoutedEventArgs e)
        {
                TreeViewItem tNode = (TreeViewItem)sender;
                if (tNode.IsSelected)
                {
                    string selectedNodeName = tNode.Name;
                    DependencyObject obj = allObjests[selectedNodeName];
                    LoadProperties(obj);
                    txtTreePath.Text = tNode.Header.ToString();
                    try
                    {
                        // ThreadStart ts = (ThreadStart)Delegate.CreateDelegate(typeof(ThreadStart), this, "highlight", true);
                        //ThreadStart ts = delegate { highlight((Control)obj,getWindowObject(tNode)); };
                        //Thread UIthread = new Thread(ts);
                        //UIthread.Start();
                        if (obj is Visual || obj is Visual3D)
                        {
                            highlight(obj, getWindowObject(tNode));
                        }
                        else
                        {
                            MessageBox.Show("Object is not Visual Element");
                        }
                    }
                    catch (Exception ex)
                    {
                        MessageBox.Show(ex.Message);
                    }
                    
                }
                e.Handled = true;
        }

        private Window getWindowObject(TreeViewItem tNode)
        {

            while ((tNode.Parent as TreeViewItem).Header != "Application")
            {
                tNode = tNode.Parent as TreeViewItem;
            }
            return allObjests[tNode.Name] as Window;
        }
       
        private void highlight(dynamic ctrl, Window win)
        {
            try
            {
                Application.Current.Dispatcher.Invoke(new Action(() =>
                {
                    
                    Grid myGrid = (Grid)FindMainGrid(win);
                    if (ctrl is Window)
                    {
                        ctrl = myGrid;
                    }
                    Canvas highlightingCanvas = new Canvas();
                    
                    SolidColorBrush brush = new SolidColorBrush();
                    brush.Opacity = 0;
                    myGrid.Background = brush;
                    
                    highlightingCanvas.Name = "highlightingCanvas";

                    if (myGrid.Children.OfType<Canvas>().Where(can => can.Name == "highlightingCanvas").Count() > 0)
                    {
                        Canvas c = myGrid.Children.OfType<Canvas>().Where(can => can.Name == "highlightingCanvas").First();
                        myGrid.Children.Remove(c);
                    } 
                    myGrid.Children.Add(highlightingCanvas);
                    int w = (int)ctrl.ActualWidth;
                    int h = (int)ctrl.ActualHeight;

                    Point relativePoint = ctrl.TransformToAncestor(myGrid).Transform(new Point(0, 0));

                    Rectangle rec = new Rectangle()
                    {
                        Width = w + 4,
                        Height = h + 4,

                        Stroke = Brushes.Red,
                        StrokeThickness = 2,
                    };

                    Canvas.SetTop(rec, relativePoint.Y - 2);
                    Canvas.SetLeft(rec, relativePoint.X - 2);
                    highlightingCanvas.Children.Add(rec);

                }));
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
        }

        private void grdPropVal_MouseDoubleClick(object sender, MouseButtonEventArgs e)
        {
            if (sender != null)
            {
                DataGrid grid = sender as DataGrid;
                if (grid != null && grid.SelectedItems != null && grid.SelectedItems.Count == 1)
                {
                    ObjectProperties objProp = (ObjectProperties)grid.SelectedItem;
                    //objProp.Name
                    LoadProperties(objProp.value);
                }
            }
        }
        
        public void LoadProperties(Object obj){
          
           propval = new List<ObjectProperties>();
            

            Type T = obj.GetType();
            IEnumerable<MemberInfo> allProps = T.GetMembers().Where(p => p.MemberType == MemberTypes.Property);

            foreach (MemberInfo propname in allProps)
            {
                try
                {
                    PropertyInfo prop = (PropertyInfo)propname;

                    propval.Add(new ObjectProperties()
                    {
                        Name = prop.Name,
                        value = prop.GetValue(obj, null)
                    });
                }
                catch (Exception)
                {
                    
                   
                }
                
            }
            grdPropVal.ItemsSource = propval;

        }

        private void txtSearch_TextChanged_1(object sender, TextChangedEventArgs e)
        {
            if (txtSearch.Text == "")
            {
                grdPropVal.ItemsSource = propval;
            }
            else
            {
                if (propval != null)
                {
                    IEnumerable<ObjectProperties> propVals = propval.Where(p => p.value != null && p.value.ToString().ToLower().Contains(txtSearch.Text.ToLower()));
                    IEnumerable<ObjectProperties> propName = propval.Where(p => p.Name.ToLower().Contains(txtSearch.Text.ToLower()));
                    grdPropVal.ItemsSource = propName.Concat(propVals);
                }
            }

        }

        private void btnRefresh_Click(object sender, RoutedEventArgs e)
        {
            allObjests.Clear();
            treeVisual.Items.Clear();
            LoadObjectTree();
        }

        private void Window_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Enter)
            {
                int x = (int)GetCursorPosition().X;
                int y = (int)GetCursorPosition().Y;
                IntPtr handle = WindowFromPoint(x, y);
                //MessageBox.Show("" + handle); 
                StringBuilder _className = new StringBuilder(1024);
                GetClassName(handle, _className, 1024);
                String className = _className.ToString();
                _className.Clear();
                MessageBox.Show("" + handle);
            }
        }

        [StructLayout(LayoutKind.Sequential)]
        public struct POINT
        {
            public int X;
            public int Y;

            public static implicit operator Point(POINT point)
            {
                return new Point(point.X, point.Y);
            }
        }

        [DllImport("user32.dll")]
        public static extern bool GetCursorPos(out POINT lpPoint);

        [DllImport("user32.dll")]
        static extern IntPtr WindowFromPoint(int xPoint, int yPoint);

        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        static extern int GetClassName(IntPtr hWnd, StringBuilder lpClassName, int nMaxCount);

        [DllImport("user32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        static extern int GetWindowText(IntPtr hWnd, StringBuilder lpString, int nMaxCount);

        public static Point GetCursorPosition()
        {
            POINT lpPoint;
            GetCursorPos(out lpPoint);
            //bool success = User32.GetCursorPos(out lpPoint);
            // if (!success)

            return lpPoint;
        }

       
       
        bool DragStarted = false;
        private void trackball_MouseDown(object sender, MouseButtonEventArgs e)
        {
            DragStarted = true;
            DragDrop.DoDragDrop(trackball, trackball.Source, DragDropEffects.All);
        }

        



    }
}

