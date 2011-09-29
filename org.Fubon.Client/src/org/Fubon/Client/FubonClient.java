package org.Fubon.Client;




import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;

public class FubonClient {

	protected Shell shell;
	TabFolder tabFolder;
	TabItem tabItem_working;
	TabItem tabItem_config;
	private Table SignalTable;
	private TableColumn ruleColumn,symbolColumn, dateColumn, priceColumn, changeColumn, volColumn, avgColumn, totalColumn,messageColumn;  
	Composite Row1;
	Composite composite_working;
	Composite composite_config;
	private Text txtTxtremoteip;
	private Text txtRemotePort;
	private Label  labelStatus;
	private int current_index=0;
	static String Server_address;
	static int Server_port;
	static Thread SocketClient_Thread;
	static SocketClient m_SocketClient;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			FubonClient window = new FubonClient();
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
		//boolean			needUpdate=false;
		createContents();
		shell.open();
		shell.layout();
		queue = SocketClient.queue;  //new Signal_Queue();
		if (queue==null) 
		{
			queue= SocketClient.queue=new Signal_Queue();
		
		}
		// testSignal();
		// testSignal();
		// testSignal();
		// testSignal();
		
		String	message="";
		while (!shell.isDisposed()) {
			synchronized(queue)
			{
			while (current_index<queue.currentIndex())
			{
				
					tsignal=queue.get(current_index);
				
				addSignal(tsignal);
				current_index++;
			}
			}
			synchronized(SocketClient.statusMessage)
			{
				message=SocketClient.statusMessage+" =>回補封包:"+((Integer)SocketClient.update_packet).toString()+"收到封包:"+((Integer)SocketClient.packet_receive).toString()+"錯誤封包:"+((Integer)SocketClient.err_packet_receive).toString();
			}
			if (labelStatus.getText().compareTo(message)!=0)
			{
				labelStatus.setText(message);
				labelStatus.setRedraw(true);
				labelStatus.setSize(message.length()*10, 15);
				
			}
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				Resize();
			}
		});
		shell.setSize(800, 600);
		shell.setText("富邦香港選股");
		
		tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
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
				
		tabItem_working = new TabItem(tabFolder, SWT.BORDER);
		tabItem_working.setText("選股結果");
		
		tabItem_config = new TabItem(tabFolder, SWT.BORDER);
		tabItem_config.setText("系統設定");

		composite_working = formToolkit.createComposite(tabFolder, SWT.BORDER);
		tabItem_working.setControl(composite_working);
		formToolkit.paintBordersFor(composite_working);
		composite_working.setLayout(new RowLayout(SWT.VERTICAL));
		
				
		Row1 = formToolkit.createComposite(composite_working, SWT.BORDER);
		Row1.setLayoutData(new RowData(770, 462));
		Row1.setLayout(new RowLayout(SWT.HORIZONTAL));
		SignalTable = new Table(Row1, SWT.BORDER | SWT.FULL_SELECTION);
		formToolkit.adapt(SignalTable,true,true);
		formToolkit.paintBordersFor(SignalTable);
		SignalTable.setHeaderVisible(true);
		SignalTable.setLinesVisible(true);
		SignalTable.setSize(new Point(Row1.getSize().x,Row1.getSize().y));
		//String config_mesg="";
/*		config_mesg += "收盤價資料檔案:" + DataManager.CloseFile +"\n";
		config_mesg += "股票代號資料檔案:" + DataManager.SymbolFile +"\n";
		config_mesg += "轉錄資料檔案:" + DataManager.RecordFile +"\n";
		config_mesg += "LOG檔案:" + DataManager.LogFile +"\n";
		config_mesg += "交易所:" + DataManager.Exchange +"\n";
		config_mesg += "開盤時間:" + DataManager.OpenTime +"\n";
*/
		
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

		
		
		composite_config = formToolkit.createComposite(tabFolder, SWT.NONE);
		tabItem_config.setControl(composite_config);
		formToolkit.paintBordersFor(composite_config);
		
		Label lblNewLabel = new Label(composite_config, SWT.NONE);
		lblNewLabel.setBounds(10, 21, 83, 16);
		formToolkit.adapt(lblNewLabel, true, true);
		lblNewLabel.setText("選股Server位址");
		
		Label lblRemotePort = new Label(composite_config, SWT.NONE);
		lblRemotePort.setText("選股ServerPort");
		lblRemotePort.setBounds(10, 52, 83, 16);
		formToolkit.adapt(lblRemotePort, true, true);
		
		txtTxtremoteip = new Text(composite_config, SWT.BORDER);
		txtTxtremoteip.setText("203.67.19.86");
		txtTxtremoteip.setBounds(94, 15, 100, 22);
		formToolkit.adapt(txtTxtremoteip, true, true);
		
		txtRemotePort = new Text(composite_config, SWT.BORDER);
		txtRemotePort.setText("777");
		txtRemotePort.setBounds(94, 46, 100, 22);
		formToolkit.adapt(txtRemotePort, true, true);
		
		labelStatus = new Label(composite_config, SWT.NONE);
		labelStatus.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		labelStatus.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		labelStatus.setText("已初始化");
		labelStatus.setBounds(10, 152,labelStatus.getText().length()*10, 15);
		

		
		Button button = new Button(composite_config, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Server_address=txtTxtremoteip.getText();
				if (SocketClient_Thread!=null) 
				{
					try
					{
						SocketClient_Thread.interrupt();
						m_SocketClient.client.close();
					}
					catch(Exception ex)
					{
						System.err.println("Close Socket Error :"+ex.toString());
					}
					while(SocketClient_Thread.isAlive()){}
				}
				
				Server_port=Integer.parseInt((txtRemotePort.getText()));
				m_SocketClient=new SocketClient(Server_address,Server_port);
				SocketClient_Thread = new Thread(m_SocketClient);
				removeAllSignal();
				SocketClient_Thread.start();
			}
		});
		button.setBounds(10, 88, 214, 26);
		formToolkit.adapt(button, true, true);
		button.setText("以上面設定連接選股伺服器");
		Resize();
	}
	void Resize()
	{
		if ((composite_working==null)||(composite_config==null)||(tabFolder==null)) return;
		Rectangle rect = shell.getClientArea ();
		if (rect.height < 550)
		{
			rect.height=550;
			shell.setSize(rect.width+20,rect.height+100);
		}	
		rect.height = rect.height - 40;
    	tabFolder.setBounds(rect);
      	composite_working.setSize(rect.width, rect.height );
      	Row1.setSize(rect.width - 20, rect.height-30);
      	
      	SignalTable.setBounds(0, 0,rect.width - 40, rect.height-40);
   		composite_config.setSize(rect.width, rect.height);
  		
  		System.out.println(rect);
	}
	
	void removeAllSignal()
	{
		/* 條件 */ /* Symbol */ /* 時間 *//* 價格 *//* 漲跌 *//* 成交量 *//* 成交總量 *//* 前X日成交均量Y% */
		
		SignalTable.removeAll();
		SignalTable.clearAll();
		current_index=0;
		Signal_Queue	queue;
		queue= SocketClient.queue;
		queue.removeall();
		
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
	
	void testSignal()
	{
		Signal tsignal;
		Signal testsignal;
		
			tsignal=new Signal("開盤後10分鐘總量(5000)>前3日成交均量 (50000)10%  ");
			tsignal.Rule = "條件一";
			tsignal.Symbol = "0001";
			tsignal.Time = "09:30:29";
			tsignal.Price = (float)34.45;
			tsignal.change = (float)0.04;
			tsignal.volume = 456578;
			tsignal.totlavolume = 789012;
			tsignal.avgvolume = 123435;
			testsignal=new  Signal("測試:開盤後10分鐘總量(5000)>前3日成交均量 (50000)10%  ");
			testsignal.setSignal(tsignal.getSignal());
			synchronized(SocketClient.queue)
			{
				SocketClient.queue.add(tsignal);
				SocketClient.queue.add(testsignal);
			}
	}
}
