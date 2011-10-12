package org.Fubon.Server.UI;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;

import org.Fubon.Server.BroadCastingServer;
import org.Fubon.Server.Signal;
import org.Fubon.Server.Signal_Queue;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
//import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
//import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
//import org.eclipse.jface.viewers.IStructuredContentProvider;
//import org.eclipse.jface.viewers.ITableLabelProvider;
//import org.eclipse.jface.viewers.LabelProvider;
//import org.eclipse.jface.viewers.TableViewer;
//import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.layout.RowLayout;
//import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;
import org.Fubon.Server.DataManager;

public class Fubon {

	protected Shell shlZ;
	private Text Condition1_txt1;
	private Text Condition1_txt2;
	private Text Condition1_txt3;
	private Text Condition2_txt1;
	private Text Condition2_txt2;
	private Text Condition3_txt1;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
//	private Text txtTxtremoteip;
//	private Text txtRemotePort;
	TabFolder tabFolder;
	TabItem tabItem_working;
	TabItem tabItem_config;
	TabItem tabItem_stock;
	Composite composite_working;
	Composite Row1,Row2,Row3,Row4,Row5,Row6,Row7,Row8,Row9;
	Composite composite_config;
	Composite composite_stock;
	Label Condition;
	Label Condition_Label;
	Label TradingDate_Label;
	Label TradingDate;
	Label Status1_Label;
	Label Status2_Label;
	Label Status1;
	Label Status2;
	Button btnCB_Condition1;
	Button btnCB_Condition1_1;
	Button btnCB_Condition2;
	Button btnCB_Condition3;
	Label lblLogFileLabel;
	private Text SymbolText;
	
	/* -----------------------------------  */
	static public DataManager m_DM;
	static Thread DataManager_Thread,BroadCastingServer_Thread;
	static BroadCastingServer m_BCServer;
	
//	private Table StockTick;
	private Text textQueryResult;
	public Text textMoniter;
	private Table SignalTable;
	private TableColumn ruleColumn,symbolColumn, dateColumn, priceColumn, changeColumn, volColumn, avgColumn, totalColumn,messageColumn;  
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Fubon window = new Fubon();
			m_DM=new DataManager();
			//m_DM.getStockCloseData();
			m_BCServer=new BroadCastingServer();
			BroadCastingServer_Thread = new Thread(m_BCServer);
			BroadCastingServer_Thread.start();
			//ClientTest CT=new ClientTest();
			//Thread t = new Thread(CT);
	        //t.start();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		Signal_Queue	queue;
		Signal			tsignal;
//		boolean			needUpdate=false;
		createContents();
		shlZ.open();
		shlZ.layout();
		queue = m_DM.queue;  //new Signal_Queue();
		if (queue==null) queue=new Signal_Queue();
		int current_index=0;
		String	message="";
		while (!shlZ.isDisposed()) {
			while (current_index<queue.currentIndex())
			{
				tsignal=queue.get(current_index);
				addSignal(tsignal);
				current_index++;
			}
			if (m_DM.currentStatus.Changed())
			textMoniter.setText(m_DM.currentStatus.read());
			message=getQuoteServerStatus();
			if (Status1.getText().compareTo(message)!=0)
			{
				Status1.setText(message);
				Status1.setRedraw(true);
				Status1.setSize(message.length()*10, 15);
				//Status2_Label.setBounds((Status1_Label.getText().length()+Status1.getText().length())*10,3,Status2_Label.getText().length()*10, 15);
				//Status2.setBounds((Status2_Label.getText().length()+Status1_Label.getText().length()+Status1.getText().length())*10,3,Status2_Label.getText().length()*10, 15);
				Status2_Label.setSize(Status2_Label.getText().length()*10, 15);
				Status2.setSize(Status2_Label.getText().length()*10, 15);

			}
			message=getBroadCastingServerStatus();
			if (Status2.getText().compareTo(message)!=0)
			{
				Status2.setText(message);
				Status2.setRedraw(true);
				Status2.setSize(message.length()*10, 15);
			}
			message=m_DM.getMarketTradingDate();
			if (TradingDate.getText().compareTo(message)!=0)
			{
				//清盤
				SignalTable.clearAll();
				TradingDate.setText(message);
				TradingDate.setRedraw(true);
				TradingDate.setSize(message.length()*10, 15);
			}
			/* message=getQuoteServerStatus();
			Status1.setText(message);
			Status1.setSize(message.length()*10, 30);
			message=getBroadCastingServerStatus();
			Status2.setText(getBroadCastingServerStatus());
			Status2.setSize(message.length()*10, 30);
			*/
			/*while ((tsignal=queue.remove())!=null)
			{
				addSignal(tsignal);
				//message= tsignal.getSignal()+"\n";
				textMoniter.setText(m_DM.currentStatus.read());
				needUpdate=true;
			}
			if (needUpdate)
			{
				//textMoniter.setText(message);
				needUpdate=false;
			}*/
			if (!display.readAndDispatch()) {
				display.sleep();
			}			
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlZ = new Shell();
		shlZ.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				MessageBox mb;
				mb = new MessageBox(shlZ, SWT.ICON_QUESTION |SWT.YES | SWT.NO);
				mb.setText("要關閉程式 要儲存股票資料嗎 ?");
				mb.setMessage("要關閉程式 要儲存股票資料嗎 ?");
				int rc = mb.open();
				if (rc == SWT.YES) 
				{
					//boolean backup_success;
					// Make sure m_DM Exit first

					try
					{
						DataManager_Thread.interrupt();
						m_DM.dr.Close();
					}
					catch(Exception e)
					{
						System.err.println("DataManger Close Data Receiveing Queue Error :"+e.toString());
					}
					while(DataManager_Thread.isAlive()){}
					m_DM.m_SM.backupmemoryDB();
				}
				System.exit(0);
			}
		});
		shlZ.setSize(800, 600);
		shlZ.setText("富邦選股");
				
		tabFolder = new TabFolder(shlZ, SWT.NONE);
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				System.out.println("widgetDefaultSelected");
			}
			@Override
			public void widgetSelected(SelectionEvent e) {
				Resize();  //To solve the switch tabe layout problem
				System.out.println("widgetSelected");
			}
		});
				
		tabItem_working = new TabItem(tabFolder, SWT.NONE);
		tabItem_working.setText("條件設定");
		
		tabItem_config = new TabItem(tabFolder, SWT.NONE);
		tabItem_config.setText("系統設定");
		
		tabItem_stock = new TabItem(tabFolder, SWT.NONE);
		tabItem_stock.setText("股票查詢");
		
	
		composite_working = formToolkit.createComposite(tabFolder, SWT.NONE);
		tabItem_working.setControl(composite_working);
		formToolkit.paintBordersFor(composite_working);
		composite_working.setLayout(new RowLayout(SWT.VERTICAL));
		
		Row1 = formToolkit.createComposite(composite_working, SWT.BORDER);
		Row1.setLayout(new RowLayout(SWT.HORIZONTAL));
		btnCB_Condition1 = new Button(Row1, SWT.CHECK);
		btnCB_Condition1.setFont(SWTResourceManager.getFont("微軟正黑體", 9, SWT.CHECK));
		btnCB_Condition1.setText("條件一");
		Label C1_label1 = new Label(Row1, SWT.NONE);
		C1_label1.setFont(SWTResourceManager.getFont("微軟正黑體", 9, SWT.NORMAL));
		C1_label1.setText("開盤後");
		
		Condition1_txt1 = new Text(Row1, SWT.BORDER);
		Condition1_txt1.setText("30");
		Condition1_txt1.addListener(SWT.Verify, new Listener() {
		      public void handleEvent(Event e) {
		          String string = e.text;
		          char[] chars = new char[string.length()];
		          string.getChars(0, chars.length, chars, 0);
		          for (int i = 0; i < chars.length; i++) {
		            if (!('0' <= chars[i] && chars[i] <= '9')) {
		              e.doit = false;
		              return;
		            }
		          }
		        }
		      });
		Label C1_label2 = new Label(Row1, SWT.NONE);
		C1_label2.setFont(SWTResourceManager.getFont("微軟正黑體", 9, SWT.NORMAL));
		C1_label2.setText("分鐘總量>前");
		
		Condition1_txt2 = new Text(Row1, SWT.BORDER);
		Condition1_txt2.setText("1");
		Condition1_txt2.addListener(SWT.Verify, new Listener() {
		      public void handleEvent(Event e) {
		          String string = e.text;
		          char[] chars = new char[string.length()];
		          string.getChars(0, chars.length, chars, 0);
		          for (int i = 0; i < chars.length; i++) {
		            if (!('0' <= chars[i] && chars[i] <= '9')) {
		              e.doit = false;
		              return;
		            }
		          }
		        }
		      });
		Label C1_label3 = new Label(Row1, SWT.NONE);
		C1_label3.setFont(SWTResourceManager.getFont("微軟正黑體", 9, SWT.NORMAL));
		C1_label3.setText("日成交均量");
		Condition1_txt3 = new Text(Row1, SWT.BORDER);
		Condition1_txt3.setText("30");
		Condition1_txt3.addListener(SWT.Verify, new Listener() {
		      public void handleEvent(Event e) {
		          String string = e.text;
		          char[] chars = new char[string.length()];
		          string.getChars(0, chars.length, chars, 0);
		          for (int i = 0; i < chars.length; i++) {
		            if (!('0' <= chars[i] && chars[i] <= '9')) {
		              e.doit = false;
		              return;
		            }
		          }
		        }
		      });
		Label C1_label4 = new Label(Row1, SWT.NONE);
		C1_label4.setFont(SWTResourceManager.getFont("微軟正黑體", 9, SWT.NORMAL));
		C1_label4.setText(" %  (");
		btnCB_Condition1_1 = new Button(Row1, SWT.CHECK);
		btnCB_Condition1_1.setFont(SWTResourceManager.getFont("微軟正黑體", 9, SWT.CHECK));
		btnCB_Condition1_1.setText("過濾剛上市股票)");
		
		Row2 = formToolkit.createComposite(composite_working, SWT.BORDER);
		Row2.setLayout(new RowLayout(SWT.HORIZONTAL));
		btnCB_Condition2 = new Button(Row2, SWT.CHECK);
		btnCB_Condition2.setFont(SWTResourceManager.getFont("微軟正黑",  9, SWT.CHECK));
		btnCB_Condition2.setText("條件二");

		Condition2_txt1 = new Text(Row2, SWT.BORDER);
		Condition2_txt1.setText("20");
		Condition2_txt1.addListener(SWT.Verify, new Listener() {
		      public void handleEvent(Event e) {
		          String string = e.text;
		          char[] chars = new char[string.length()];
		          string.getChars(0, chars.length, chars, 0);
		          for (int i = 0; i < chars.length; i++) {
		            if (!('0' <= chars[i] && chars[i] <= '9')) {
		              e.doit = false;
		              return;
		            }
		          }
		        }
		      });
		Label C2_labe1 = new Label(Row2, SWT.NONE);
		C2_labe1.setFont(SWTResourceManager.getFont("微軟正黑體", 9, SWT.NORMAL));
		C2_labe1.setText("分鐘內漲幅>");		
		Condition2_txt2 = new Text(Row2, SWT.BORDER);
		Condition2_txt2.setText("3");
		Condition2_txt2.addListener(SWT.Verify, new Listener() {
		      public void handleEvent(Event e) {
		          String string = e.text;
		          char[] chars = new char[string.length()];
		          string.getChars(0, chars.length, chars, 0);
		          for (int i = 0; i < chars.length; i++) {
		            if (!('0' <= chars[i] && chars[i] <= '9')) {
		              e.doit = false;
		              return;
		            }
		          }
		        }
		      });
		Label C2_labe2 = new Label(Row2, SWT.NONE);
		C2_labe2.setFont(SWTResourceManager.getFont("微軟正黑體", 9, SWT.NORMAL));
		C2_labe2.setText("%");		
		
		Row3 = formToolkit.createComposite(composite_working, SWT.BORDER);
		Row3.setLayout(new RowLayout(SWT.HORIZONTAL));
		btnCB_Condition3 = new Button(Row3, SWT.CHECK);
		btnCB_Condition3.setFont(SWTResourceManager.getFont("微軟正黑",  9, SWT.CHECK));
		btnCB_Condition3.setText("條件三");
		Label C3_labe1 = new Label(Row3, SWT.NONE);
		C3_labe1.setFont(SWTResourceManager.getFont("微軟正黑體",  9, SWT.NONE));
		C3_labe1.setText("單量大於");

				
		Condition3_txt1 = new Text(Row3, SWT.BORDER);
		Condition3_txt1.setText("300");
		Condition3_txt1.addListener(SWT.Verify, new Listener() {
		      public void handleEvent(Event e) {
		          String string = e.text;
		          char[] chars = new char[string.length()];
		          string.getChars(0, chars.length, chars, 0);
		          for (int i = 0; i < chars.length; i++) {
		            if (!('0' <= chars[i] && chars[i] <= '9')) {
		              e.doit = false;
		              return;
		            }
		          }
		        }
		      });

		Label C3_labe2 = new Label(Row3, SWT.NONE);
		C3_labe2.setFont(SWTResourceManager.getFont("微軟正黑體",  9, SWT.NONE));
		C3_labe2.setText("張");
		
		Row4 = formToolkit.createComposite(composite_working, SWT.BORDER);	
		Row4.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		TradingDate_Label = new Label(Row4, SWT.NONE);
		TradingDate_Label.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		TradingDate_Label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		TradingDate_Label.setText("交易日期:");
		TradingDate = new Label(Row4, SWT.NONE);
		TradingDate.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		TradingDate.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		TradingDate.setText("-N/A-");
		
		Condition_Label = new Label(Row4, SWT.NONE);
		Condition_Label.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		Condition_Label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		Condition_Label.setText("條件設定:");
		Condition = new Label(Row4, SWT.NONE);
		Condition.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		Condition.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		Condition.setText("還未設定");
		
		
		Row5 = formToolkit.createComposite(composite_working, SWT.BORDER);	
		Row5.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		
		Status1_Label = new Label(Row5, SWT.NONE);
		Status1_Label.setText("報價Server狀態:");
		Status1_Label.setSize(Status1_Label.getText().length()*10, 15);
		Status1 = new Label(Row5, SWT.NONE);
		Status1.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		Status1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		Status1.setText(getQuoteServerStatus());
		Status1.setSize(Status1.getText().length()*10, 15);
		
		Row6 = formToolkit.createComposite(composite_working, SWT.BORDER);	
		Row6.setLayout(new RowLayout(SWT.HORIZONTAL));
		



		Button ConditionSendbutton = new Button(Row6, SWT.NONE);
		ConditionSendbutton.setText("條件送出");
		ConditionSendbutton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String ConditionMessage="";
		        int style=0;
		        style |= SWT.ICON_ERROR;
		        MessageBox mb;
				Integer Condition1_Min,Condition1_Day,Condition1_Percent;

				Integer Condition2_Min,Condition2_Percent;
				
				Integer Condition3_Vol;
				
				boolean Condition1_Filter;
				
				if (btnCB_Condition1.getSelection())
				{
					
					Condition1_Min=Integer.parseInt(Condition1_txt1.getText().length()==0 ? "0" : Condition1_txt1.getText());
			        // Display the message box
					if (Condition1_Min==0)
					{
						mb = new MessageBox(shlZ, style);
						mb.setText("條件輸入錯誤");
						mb.setMessage("條件一 開盤後分鐘數 不能沒填或為0 !");
						mb.open();
						return;
					}
					Condition1_Day=Integer.parseInt(Condition1_txt2.getText().length()==0 ? "0" : Condition1_txt2.getText());
					if (Condition1_Day==0)
					{
						mb = new MessageBox(shlZ, style);
						mb.setText("條件輸入錯誤");
						mb.setMessage("條件一 前幾日 不能沒填或為0 !");
						mb.open();
						return;
					}
					Condition1_Percent=Integer.parseInt(Condition1_txt3.getText().length()==0 ? "0" : Condition1_txt3.getText());
					if (Condition1_Percent==0)
					{
						mb = new MessageBox(shlZ, style);
						mb.setText("條件輸入錯誤");
						mb.setMessage("條件一 百分比 不能沒填或為0 !");
						mb.open();
						return;
					}
					Condition1_Filter=btnCB_Condition1_1.getSelection();
					m_DM.setRule1(Condition1_Min, Condition1_Day, Condition1_Percent, Condition1_Filter, true);
					ConditionMessage=ConditionMessage+"條件一:開盤後"+Condition1_Min.toString()+"分鐘總量>前"+Condition1_Day.toString()+"日成交均量"+Condition1_Percent.toString()+"%";
				}
				else
				{
					Condition1_Filter=btnCB_Condition1_1.getSelection();
					m_DM.setRule1(0, 0, 0, Condition1_Filter, false);
				}
				if (btnCB_Condition2.getSelection())
				{
					Condition2_Min=Integer.parseInt(Condition2_txt1.getText().length()==0 ? "0" : Condition2_txt1.getText());
					if (Condition2_Min==0)
					{
						mb = new MessageBox(shlZ, style);
						mb.setText("條件輸入錯誤");
						mb.setMessage("條件二 分鐘內漲幅 分鐘數 不能沒填或為0 !");
						mb.open();
						return;
					}
					Condition2_Percent=Integer.parseInt(Condition2_txt2.getText().length()==0 ? "0" : Condition2_txt2.getText());
					if (Condition2_Percent==0)
					{
						mb = new MessageBox(shlZ, style);
						mb.setText("條件輸入錯誤");
						mb.setMessage("條件二 百分比 不能沒填或為0 !");
						mb.open();
						return;
					}
					m_DM.setRule2(Condition2_Min, Condition2_Percent, true);
					ConditionMessage=ConditionMessage+"條件二:"+Condition2_Min.toString()+"分鐘內漲幅>"+Condition2_Percent.toString()+"%";
				}
				else
					m_DM.setRule2(0, 0, false);
				if (btnCB_Condition3.getSelection())
				{
					Condition3_Vol=Integer.parseInt(Condition3_txt1.getText().length()==0 ? "0" : Condition3_txt1.getText());
					if (Condition3_Vol==0)
					{
						mb = new MessageBox(shlZ, style);
						mb.setText("條件輸入錯誤");
						mb.setMessage("條件三 單量上限 不能沒填或為0 !");
						mb.open();
						return;
					}
					m_DM.setRule3(Condition3_Vol, true);
					ConditionMessage=ConditionMessage+"條件三:單量大於"+Condition3_Vol.toString()+"張";
				}
				else
					m_DM.setRule3(0, false);
				Condition.setText(ConditionMessage);
				Condition.setSize(Row6.getSize().x-Condition_Label.getSize().x, 15);
				SignalTable.removeAll();
				SignalTable.clearAll();
			}
		});
				
		Button StartDataReading = new Button(Row6, SWT.NONE);
		StartDataReading.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				m_DM.setReadingMethod(0);
				StartReceiving() ;
			}
		});
		
		Button StartDataReadingfromFile = new Button(Row6, SWT.NONE);
		StartDataReadingfromFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				m_DM.setReadingMethod(1);
				StartReceiving() ;
			}
		});
		
		Button resetBroadCastingServer = new Button(Row6, SWT.NONE);
		resetBroadCastingServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetBroadCastingServer() ;
			}
		});
		
		
		formToolkit.adapt(StartDataReading, true, true);
		StartDataReading.setText("開始接收資料");
	
		formToolkit.adapt(StartDataReadingfromFile, true, true);
		StartDataReadingfromFile.setText("從檔案接收資料");
		
		formToolkit.adapt(resetBroadCastingServer, true, true);
		resetBroadCastingServer.setText("重設推送訊號伺服器");
		
/*		Button btnNewButton_1 = new Button(composite_working, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textMoniter.setText(message);
			}
		});
		formToolkit.adapt(btnNewButton_1, true, true);
		btnNewButton_1.setText("更新");
*/		
		
		Row7 = formToolkit.createComposite(composite_working, SWT.BORDER);	
		Row7.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Status2_Label = new Label(Row7, SWT.NONE);
		Status2_Label.setText("BroadCasting Server狀態:");
		//Status2_Label.setBounds((Status1_Label.getText().length()+Status1.getText().length())*10,3,Status2_Label.getText().length()*10, 15);
		Status2_Label.setSize(40, 15);
		Status2 = new Label(Row7, SWT.NONE);
		Status2.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		Status2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		Status2.setText(getBroadCastingServerStatus());
		Status2.setSize(60, 15);
		//Status2.setBounds((Status2_Label.getText().length()+Status1_Label.getText().length()+Status1.getText().length())*10,3,Status2_Label.getText().length()*10, 15);

		Row8 = formToolkit.createComposite(composite_working, SWT.BORDER);
		Row8.setLayout(new RowLayout(SWT.HORIZONTAL));
		textMoniter = new Text(Row8, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL);
		formToolkit.adapt(textMoniter, true, true);
		textMoniter.setSize(new Point(composite_working.getSize().x,15));
		textMoniter.setText("Test Test Test .......");
		
		Row9 = formToolkit.createComposite(composite_working, SWT.BORDER);
		Row9.setLayout(new RowLayout(SWT.HORIZONTAL));
		SignalTable = new Table(Row9, SWT.BORDER | SWT.FULL_SELECTION);
		formToolkit.adapt(SignalTable,true,true);
		formToolkit.paintBordersFor(SignalTable);
		SignalTable.setHeaderVisible(true);
		SignalTable.setLinesVisible(true);
		SignalTable.setSize(new Point(Row9.getSize().x,Row9.getSize().y));
		
		/* 設定 Signal Table Column
		 * 
		 */
		
		/* 條件 */
		ruleColumn = new TableColumn(SignalTable, SWT.CENTER);
		ruleColumn.setText("條件");
		ruleColumn.setWidth(50);
		ruleColumn.addListener(SWT.Selection, SortListenerFactory.getListener(SortListenerFactory.STRING_COMPARATOR));

		/* Symbol */
		symbolColumn = new TableColumn(SignalTable, SWT.CENTER);
		symbolColumn.setText("代號");
		symbolColumn.setWidth(50);
		symbolColumn.addListener(SWT.Selection, SortListenerFactory.getListener(SortListenerFactory.STRING_COMPARATOR));

		/* 時間 */
		dateColumn = new TableColumn(SignalTable, SWT.CENTER);
        dateColumn.setText("時間");
        dateColumn.setWidth(60);
        dateColumn.addListener(SWT.Selection, SortListenerFactory.getListener(SortListenerFactory.HOUR_COMPARATOR));

        /* 價格 */
	    priceColumn = new TableColumn(SignalTable, SWT.CENTER);
	    priceColumn.setText("價格");
	    priceColumn.setWidth(50);
	    priceColumn.addListener(SWT.Selection, SortListenerFactory.getListener(SortListenerFactory.DOUBLE_COMPARATOR));

        /* 漲跌 */
	    changeColumn = new TableColumn(SignalTable, SWT.CENTER);
	    changeColumn.setText("漲跌");
	    changeColumn.setWidth(50);
	    changeColumn.addListener(SWT.Selection, SortListenerFactory.getListener(SortListenerFactory.DOUBLE_COMPARATOR));

        /* 成交量 */
        
		volColumn = new TableColumn(SignalTable, SWT.CENTER);
	    volColumn.setText("成交量");
	    volColumn.setWidth(50);
	    volColumn.addListener(SWT.Selection, SortListenerFactory.getListener(SortListenerFactory.INT_COMPARATOR));

        /* 成交總量 */
        
		totalColumn = new TableColumn(SignalTable, SWT.CENTER);
		totalColumn.setText("成交總量");
		totalColumn.setWidth(60);
		totalColumn.addListener(SWT.Selection, SortListenerFactory.getListener(SortListenerFactory.INT_COMPARATOR));

	    /* 前X日成交均量Y% */
		avgColumn = new TableColumn(SignalTable, SWT.CENTER);
	    avgColumn.setText("前幾日成交均量");
	    avgColumn.setWidth(60);
	    avgColumn.addListener(SWT.Selection, SortListenerFactory.getListener(SortListenerFactory.INT_COMPARATOR));
	    
	    /* 訊息 */
	    messageColumn = new TableColumn(SignalTable, SWT.CENTER);
	    messageColumn.setText("訊息");
	    messageColumn.setWidth(180);
	    messageColumn.addListener(SWT.Selection, SortListenerFactory.getListener(SortListenerFactory.STRING_COMPARATOR));

	
/*
				 dateColumn = new TableColumn(SignalTable, SWT.CENTER);
		         dateColumn.setText("Date Column");
		         dateColumn.setWidth(120);
		         dateColumn.addListener(SWT.Selection, SortListenerFactory.getListener(SortListenerFactory.DATE_COMPARATOR));

				 intColumn = new TableColumn(SignalTable, SWT.CENTER);
			     intColumn.setText("Number Column");
			     intColumn.setWidth(120);
			     intColumn.addListener(SWT.Selection, SortListenerFactory.getListener(SortListenerFactory.INT_COMPARATOR));
			        
			     stringColumn = new TableColumn(SignalTable, SWT.CENTER);
			     stringColumn.setText("String Column");
			     stringColumn.setWidth(120);
			     stringColumn.addListener(SWT.Selection, SortListenerFactory.getListener(SortListenerFactory.STRING_COMPARATOR));
			        
			        			        
			     doubleColumn = new TableColumn(SignalTtable, SWT.CENTER);
			     doubleColumn.setText("Double Column");
			     doubleColumn.setWidth(120);
			     doubleColumn.addListener(SWT.Selection, SortListenerFactory.getListener(SortListenerFactory.DOUBLE_COMPARATOR));
			        
			     hourColumn = new TableColumn(SignalTable, SWT.CENTER);
			     hourColumn.setText("Hour Column");
			     hourColumn.setWidth(120);
			     hourColumn.addListener(SWT.Selection, SortListenerFactory.getListener(SortListenerFactory.HOUR_COMPARATOR));
*/
		
		composite_config = formToolkit.createComposite(tabFolder, SWT.NONE);
		tabItem_config.setControl(composite_config);
		formToolkit.paintBordersFor(composite_config);
		
/*		Label lblNewLabel = new Label(composite_config, SWT.NONE);
		lblNewLabel.setBounds(10, 21, 83, 16);
		formToolkit.adapt(lblNewLabel, true, true);
		lblNewLabel.setText("Remote IP");
		
		Label lblRemotePort = new Label(composite_config, SWT.NONE);
		lblRemotePort.setText("Remote Port");
		lblRemotePort.setBounds(10, 52, 83, 16);
		formToolkit.adapt(lblRemotePort, true, true);
		
		txtTxtremoteip = new Text(composite_config, SWT.BORDER);
		txtTxtremoteip.setText("127.0.0.1");
		txtTxtremoteip.setBounds(94, 15, 100, 22);
		formToolkit.adapt(txtTxtremoteip, true, true);
		
		txtRemotePort = new Text(composite_config, SWT.BORDER);
		txtRemotePort.setText("777");
		txtRemotePort.setBounds(94, 46, 100, 22);
		formToolkit.adapt(txtRemotePort, true, true);
		
	
		Label lblLogFile = new Label(composite_config, SWT.NONE);
		lblLogFile.setText("Log File");
		lblLogFile.setBounds(10, 84, 83, 16);
		formToolkit.adapt(lblLogFile, true, true);
		
		lblLogFileLabel = new Label(composite_config, SWT.NONE);
		lblLogFileLabel.setBounds(110, 84, 300, 16);
		formToolkit.adapt(lblLogFileLabel, true, true);
		lblLogFileLabel.setText("");
		
		Button btnNewButton = new Button(composite_config, SWT.NONE);
		btnNewButton.addSelectionListener(new Select());
		btnNewButton.setBounds(94, 84, 8, 22);
		formToolkit.adapt(btnNewButton, true, true);
		btnNewButton.setText("..");
		*/

		String config_mesg="";
		Label textConfig = new Label(composite_config, SWT.NONE);
		formToolkit.adapt(textConfig, true, true);
		textConfig.setBounds(shlZ.getClientArea ());
		config_mesg += "收盤價資料檔案:" + m_DM.m_SM.CloseFile +"\n";
		config_mesg += "股票代號資料檔案:" + m_DM.m_SM.SymbolFile +"\n";
		config_mesg += "轉錄資料檔案:" + m_DM.RecordFile +"\n";
		config_mesg += "LOG檔案:" + m_DM.LogFile +"\n";
		config_mesg += "交易所:" + m_DM.m_SM.Exchange +"\n";
		config_mesg += "開盤時間:" + m_DM.m_SM.strOpenTime +"\n";
		textConfig.setText(config_mesg);
		
		
		composite_stock = formToolkit.createComposite(tabFolder, SWT.NONE);
		tabItem_stock.setControl(composite_stock);
		formToolkit.paintBordersFor(composite_stock);
		
		Label lblSymbolLabel = new Label(composite_stock, SWT.NONE);
		lblSymbolLabel.setBounds(10, 10, 60, 16);
		formToolkit.adapt(lblSymbolLabel, true, true);
		lblSymbolLabel.setText("股票代號");
		
		SymbolText = new Text(composite_stock, SWT.BORDER);
		SymbolText.setBounds(76, 7, 73, 22);
		formToolkit.adapt(SymbolText, true, true);
		
		

/*				
				final TableViewer tableViewer = new TableViewer(composite_stock, SWT.BORDER | SWT.FULL_SELECTION);
				StockTick = tableViewer.getTable();
				StockTick.setBounds(10, 332, 85, 85);
				formToolkit.paintBordersFor(StockTick);
				final TableColumn newColumnTableColumn = new TableColumn(StockTick, SWT.NONE);
		        newColumnTableColumn.setWidth(10);
		        newColumnTableColumn.setText("時間");

		        final TableColumn newColumnTableColumn_1 = new TableColumn(StockTick, SWT.NONE);
		        newColumnTableColumn_1.setWidth(15);
		        newColumnTableColumn_1.setText("賣價");
		        
		        final TableColumn newColumnTableColumn_2 = new TableColumn(StockTick, SWT.NONE);
		        newColumnTableColumn_2.setWidth(15);
		        newColumnTableColumn_2.setText("買價");

		        final TableColumn newColumnTableColumn_3 = new TableColumn(StockTick, SWT.NONE);
		        newColumnTableColumn_3.setWidth(20);
		        newColumnTableColumn_3.setText("成交");

		        final TableColumn newColumnTableColumn_4 = new TableColumn(StockTick, SWT.NONE);
		        newColumnTableColumn_4.setWidth(20);
		        newColumnTableColumn_4.setText("量");
*/		        
       		        
        Button BeginQuery = new Button(composite_stock, SWT.NONE);
		BeginQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String symbol,result;
				symbol=SymbolText.getText();
				result=m_DM.Dump(symbol);
				textQueryResult.setText(result);
	/*			//textQueryResult.setText(m_DM.Dump(SymbolText.getText()));
				//設置內容器
				tableViewer.setContentProvider(new TickContentProvider());
				//設置標籤器
				tableViewer.setLabelProvider(new TickTableLabelProvider());
				//把數據集合給tableView
				if (SymbolText.getText().length()>0)
				tableViewer.setInput(m_DM.getStockTickListbySymbol(SymbolText.getText()));
				//這樣利用內容器和標籤器就能從setInput得到的數據集合分解出顯示表格需要的數據。這是一個典型的mvc的實現.
				//textQueryResult.setText("");
				 * 
				 */
			}
		});
		
		textQueryResult = new Text(composite_stock, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL);
	    textQueryResult.setEditable(false);
	    textQueryResult.setBounds(22, 56, 486, 235);
	    BeginQuery.setBounds(155, 5, 78, 26);
	    
	    formToolkit.adapt(textQueryResult, true, true);
		formToolkit.adapt(BeginQuery, true, true);
		BeginQuery.setText("開始查詢");
		
		shlZ.addListener (SWT.Resize,  new Listener () {
			    public void handleEvent (Event e) {
			    	Resize();
			    }
			  });
		//Resize();
	}
	
/*	void DBTest()
	{
	        Class.forName("org.h2.Driver");
	        Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
	        // add application code here
	        conn.close();

	}
	*/
	
	void Resize()
	{
		if ((composite_working==null)||(composite_config==null)||(composite_stock==null)||(tabFolder==null)) return;
		Rectangle rect = shlZ.getClientArea ();
		if (!((rect.height >= 600)&&(rect.width >= 600)))
		{
			if (rect.height < 600)
			{
				rect.height=600;
				
			}	
			if (rect.width < 600)
			{
				rect.width=600;
				
			}	
			//shlZ.setSize(rect.width,rect.height);
			//return;
		}
		
		tabFolder.setSize(rect.width, rect.height);
      	composite_working.setSize(rect.width, rect.height );
      	Row1.setSize(rect.width - 20, 30);
      	Row2.setSize(rect.width - 20, 30);
      	Row3.setSize(rect.width - 20, 30);
      	Row4.setSize(rect.width - 20, 30);
      	Row5.setSize(rect.width - 20, 30);
      	Row6.setSize(rect.width - 20, 30);
      	Row7.setSize(rect.width - 20, 30);
      	Row8.setSize(rect.width - 20, 30);
      	//Row9.setSize(rect.width - 20, rect.height-220);
      	Row9.setLocation(3, 270);
      	Row9.setSize(rect.width - 20, rect.height-320);
      	textMoniter.setSize(rect.width - 40, 30);
      	SignalTable.setSize(rect.width - 25, rect.height-325);
   		composite_config.setSize(rect.width, rect.height);
  		composite_stock.setSize(rect.width, rect.height);
  		System.out.println(rect);
	}
	void addSignal(Signal tsignal)
	{
		/* 條件 */ /* Symbol */ /* 時間 *//* 價格 *//* 漲跌 *//* 成交量 *//* 成交總量 *//* 前X日成交均量Y% */
		TableItem item = new TableItem(SignalTable, SWT.NONE);
        item.setText(0, tsignal.Rule);
        item.setText(1, tsignal.Symbol);
        item.setText(2, tsignal.Time);
        item.setText(3, Float.toString(tsignal.Price));
        item.setText(4, Float.toString(tsignal.change));
        item.setText(5, Long.toString(tsignal.volume));
        item.setText(6, Long.toString(tsignal.totlavolume));
        item.setText(7, Long.toString(tsignal.avgvolume));
        item.setText(8, tsignal.message);
        
	}
	
	void resetBroadCastingServer() 
	{

		MessageBox mb;
		mb = new MessageBox(shlZ, SWT.ICON_QUESTION |SWT.YES | SWT.NO);
		mb.setText("已經在接受傳送信號中 要重設傳送信號Server ?");
		mb.setMessage("已經在接受傳送信號中 要重設傳送信號Server ?");
		int rc = mb.open();
		if (rc == SWT.YES) 
		{
			if (BroadCastingServer_Thread!=null) 
			{
				try
				{
					BroadCastingServer_Thread.interrupt();
					BroadCastingServer.listener.close();
				}
				catch(Exception e)
				{
					System.err.println("Close Socket Error :"+e.toString());
				}
				while(BroadCastingServer_Thread.isAlive()){}
			}
			
			m_BCServer=new BroadCastingServer();
			//m_DM.CleanMarket();
			BroadCastingServer_Thread = new Thread(m_BCServer);
			BroadCastingServer_Thread.start();
		}
	}
	
	String getBroadCastingServerStatus() 
	{
		/*
		static int	  DataManager_Status=0;  // 0:waiting  1:receiving -1 error
		static int Reading_Type;   // 0: reading from Socket  1:reading from file
		*/
		String result="";
		if (BroadCastingServer.status==0)
			result="正常 使用人數"+((Integer)BroadCastingServer.user_numer).toString()+ " 人";
		else
			result="錯誤 ";
		
		return result;
	}
	
	String getQuoteServerStatus()
	{
		/*
		static int	  DataManager_Status=0;  // 0:waiting  1:receiving -1 error
		static int Reading_Type;   // 0: reading from Socket  1:reading from file
		*/
		String result="";
		if (m_DM.Reading_Type==0)
			result="從報價Server讀取 ";
		else
			result="從預錄檔案讀取 ";
		switch (m_DM.DataManager_Status)
		{
		case 0:
			result="等待連接";
			break;
		case 1:
			result=result+"接收中"+m_DM.currentStatusMessage;
			break;
		case -1:
			result="讀取錯誤"+m_DM.currentStatusMessage;
			break;
		default:
			result="錯誤"+m_DM.currentStatusMessage;
			break;
		}
		return result;
	}
	
	
	void StartReceiving() 
	{
		if (m_DM.DataManager_Status!=1)
		{
			
			//m_DM=new DataManager();
			//m_DM.CleanMarket();
			DataManager_Thread = new Thread(m_DM);
			DataManager_Thread.start();
		}
		if (m_DM.DataManager_Status==1)
		{
			MessageBox mb;
			mb = new MessageBox(shlZ, SWT.ICON_QUESTION |SWT.YES | SWT.NO);
			mb.setText("已經在接受Tick中 要重新連接 ?");
			mb.setMessage("已經在接受Tick中 要重新連接 ?");
			int rc = mb.open();
			if (rc == SWT.YES) 
			{
				if (DataManager_Thread!=null) 
				{
					
					try
					{
						DataManager_Thread.interrupt();
						m_DM.dr.Close();
					}
					catch(Exception e)
					{
						System.err.println("DataManger Close Data Receiveing Queue Error :"+e.toString());
					}
					while(DataManager_Thread.isAlive()){}
				}
				m_DM=new DataManager();
				//m_DM.CleanMarket();
				DataManager_Thread = new Thread(m_DM);
				DataManager_Thread.start();
			}
		}
	}
	
	 class Select implements SelectionListener {
	      public void widgetSelected(SelectionEvent event) {
	        FileDialog fd = new FileDialog(shlZ, SWT.OPEN);
	        fd.setText("Open");
	        fd.setFilterPath("C:/");
	        String[] filterExt = { "*.txt", "*.doc", ".rtf", "*.*" };
	        fd.setFilterExtensions(filterExt);
	        lblLogFileLabel.setText(fd.open());
	        System.out.println(lblLogFileLabel.getText());
	      }

	      public void widgetDefaultSelected(SelectionEvent event) {
	      }
	    }
	 
	 
	 
/*	public void dumpQueryResult()
	{
		String result;
		Map<Integer, Tick>  ts;
		Map<Date, DayLine> dl;
		Tick tTick;
		DayLine tDayLine;
		ts=m_DM.getStockTickListbySymbol(SymbolText.getText());
		dl=m_DM.getStockDayLinebySymbol(SymbolText.getText());
		for(int i = 0; i < ts.size(); i++) {
			  tTick=ts.get(i);
			  System.out.println(" Index:"+tTick.TickIndex+" Time:"+tTick.Time+" Ask:"+Float.toString(tTick.Ask)+" Bid:"+Float.toString(tTick.Bid)+" Price:"+Float.toString(tTick.Price));		 
		  }
		for(int i = 0; i < dl.size(); i++) {
			  tDayLine=dl.get(i);
			  System.out.println(" Date:"+tDayLine.tDate.toString()+" Close:"+tDayLine.Close+" Volume:"+tDayLine.Volume);		 
		  }		
	}


	 
	 public class TickContentProvider implements IStructuredContentProvider {
	        public Object[] getElements(Object inputElement) {
	            if(inputElement instanceof List){
	                return ((List)inputElement).toArray();
	            }else{
	                return new Object[0];
	            }
	        }
	        public void dispose() {
	        }
	        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	        }
	    }
	 
	 public class TickTableLabelProvider extends LabelProvider implements ITableLabelProvider {
	        public String getColumnText(Object element, int columnIndex) {
	            if (element instanceof Tick){
	                Tick p = (Tick)element;
	                if(columnIndex == 0){
	                    return p.Time.toString();
	                }else if(columnIndex == 1){
	                    return Float.toString(p.Bid);
	                }else if (columnIndex ==2){
	                    return Float.toString(p.Ask);
	                }else if (columnIndex == 3){
	                    return Float.toString(p.Price);
	                }else if (columnIndex == 4){
	                	 return Float.toString(p.Volume);
	                }
	            }
	            return null;
	        }
	        public Image getColumnImage(Object element, int columnIndex) {
	            return null;
	        }
	    }
*/
}
