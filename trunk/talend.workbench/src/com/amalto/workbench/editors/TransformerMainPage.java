/*
 * Created on 27 oct. 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.amalto.workbench.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.actions.ProcessFileThruTransformerAction;
import com.amalto.workbench.dialogs.PluginDetailsDialog;
import com.amalto.workbench.dialogs.ProcessFileDialog;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.Version;
import com.amalto.workbench.utils.WidgetUtils;
import com.amalto.workbench.webservices.WSGetTransformerPluginV2Details;
import com.amalto.workbench.webservices.WSGetTransformerPluginV2SList;
import com.amalto.workbench.webservices.WSTransformerPluginV2Details;
import com.amalto.workbench.webservices.WSTransformerPluginV2SList;
import com.amalto.workbench.webservices.WSTransformerPluginV2SListItem;
import com.amalto.workbench.webservices.WSTransformerPluginV2VariableDescriptor;
import com.amalto.workbench.webservices.WSTransformerProcessStep;
import com.amalto.workbench.webservices.WSTransformerV2;
import com.amalto.workbench.webservices.WSTransformerVariablesMapping;
import com.amalto.workbench.webservices.XtentisPort;
import com.amalto.workbench.widgets.LabelCombo;

public class TransformerMainPage extends AMainPageV2 {

	public final static String DEFAULT_VAR = "_DEFAULT_";
	public final static String DEFAULT_DISPLAY = "{}";
	
	public final static String TRANSFORMER_PLUGIN="amalto/local/transformer/plugin/";
	
	protected Text descriptionText;
	
	//protected Text inputText;
	protected Text stepText;
	//protected Text outputText;
	protected List stepsList;
	
	protected Label stepLabel;
	//protected Text jndiText;
	protected CCombo pluginsCombo;
	String currentPluginName;
	
	protected Text pluginDescription;
	protected TextViewer parametersTextViewer;
	protected ProcessFileDialog processFileDialog;

	protected DropTarget windowTarget;	
	
	protected AbstractFormPart topPart;
	
	protected boolean refreshing = false;
	protected boolean comitting = false;
	
	protected String filePath;
	
	protected int currentPlugin = -1; 
	
	protected boolean version_greater_2_16_0 = false;
	protected boolean version_greater_2_17_0 =false;
	
	protected TreeMap<String, String> pluginDescriptions = new TreeMap<String, String>();
	
	protected TreeMap<String, java.util.List<String>> inputVariablesMap = new TreeMap<String, java.util.List<String>>();
	protected TreeMap<String, java.util.List<String>> outputVariablesMap = new TreeMap<String, java.util.List<String>>();
	private XtentisPort    port;
	private TransformerStepWidget stepWidget;
	private Button disabledButton;
	private WSTransformerV2 transformer;
	private Composite specsComposite;
		
    public TransformerMainPage(FormEditor editor) {
        super(
        		editor,
        		TransformerMainPage.class.getName(),
        		"Transformer "+((XObjectEditorInput)editor.getEditorInput()).getName()
        );
        //get Version information
        try {
        	Version ver = Util.getVersion(getXObject());
        	version_greater_2_16_0 = (
        			(ver.getMajor()>2) ||
        			((ver.getMajor()==2)&&(ver.getMinor()>=16))
        	);
        	version_greater_2_17_0 = (
        			(ver.getMajor()>2) ||
        			((ver.getMajor()==2)&&(ver.getMinor()>=17))
        	);
        } catch (Exception e) {/*no versioning support on old cores*/}
    }

    @Override
	protected void createCharacteristicsContent(FormToolkit toolkit, Composite topComposite) {
    	try {
    	    port=Util.getPort(getXObject());
    	    transformer = (WSTransformerV2) getXObject().getWsObject();
    	    
            //Description and File Process
            Composite descriptionComposite = toolkit.createComposite(topComposite,SWT.NONE);
            descriptionComposite.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            descriptionComposite.setLayout(
            	new GridLayout(
            		3 ,
            		false
            	));

            //description
            Label descriptionLabel = toolkit.createLabel(descriptionComposite, "Description", SWT.NULL);
            descriptionLabel.setLayoutData(
                    new GridData(SWT.FILL,SWT.CENTER,false,true,1,1)
            );
            descriptionText = toolkit.createText(descriptionComposite, "",SWT.BORDER|SWT.MULTI);
            descriptionText.setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            ((GridData)descriptionText.getLayoutData()).minimumHeight = 30;
            descriptionText.addModifyListener(new ModifyListener() {
            	public void modifyText(ModifyEvent e) {
            		if (refreshing) return;
            		//commit as we go
            		TransformerMainPage.this.comitting= true;
            		((WSTransformerV2)getXObject().getWsObject()).setDescription(descriptionText.getText());
            		TransformerMainPage.this.comitting= false;
            		markDirty();
            	}
            });
            
            
	            //File Process
	            Button processButton = toolkit.createButton(descriptionComposite,"Process a File...",SWT.PUSH | SWT.TRAIL);
	            processButton.setLayoutData(
	                    new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
	            );
	            processButton.addSelectionListener(new SelectionListener() {
	            	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
	            	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
	            		try {
	            			//check if we have a step to perfom
	            			WSTransformerProcessStep[] steps = ((WSTransformerV2)getXObject().getWsObject()).getProcessSteps();
	            			if ((steps==null) || (steps.length == 0)) {
	            				MessageDialog.openError(TransformerMainPage.this.getSite().getShell(), "Unable to process a file", "The transformer must have at least one step!");
	            				return;
	            			}
	            			//perform save
	            			if (TransformerMainPage.this.getEditor().isDirty()) {
	            				if (MessageDialog.openConfirm(TransformerMainPage.this.getSite().getShell(), "Executing the Transformer", "The Transformer was changed and will be executed using the saved version.\nSave the transformer before executing it?"))
	            					TransformerMainPage.this.getEditor().doSave(new NullProgressMonitor());
	            			}
	            			//Open form Dialog
	    					FileDialog fd = new FileDialog(TransformerMainPage.this.getSite().getShell(),SWT.OPEN);
	    					fd.setText("Select document to upload");
	    					/*
	    					fd.setFilterExtensions(new String[] {"*.*"});
	    					fd.setFilterExtensions(new String[] {"All Files"});
	    					*/
	    					fd.setFilterExtensions(new String[] {"*.*","*.txt","*.xml"});
	    					if (filePath != null) 
	    						fd.setFilterPath(filePath+"/xxxyyyyzzzz");
	    					else
	    						fd.setFilterPath(System.getProperty("user.home")+"/xxxxxyyyyzzzz");
	    					fd.open();
	    					if ("".equals(fd.getFileName())) return;
	    					filePath = fd.getFilterPath();
	    					String filename =  fd.getFilterPath()+System.getProperty("file.separator")+fd.getFileName();
	    					processFileDialog = new ProcessFileDialog(
    							getXObject(),
    							filename,
    							TransformerMainPage.this.getSite().getShell(),
    							"Process using Transformer "+((WSTransformerV2)getXObject().getWsObject()).getName(),
    							new SelectionListener() {
    								public void 
    								widgetDefaultSelected(SelectionEvent e) {}
    								public void widgetSelected(SelectionEvent e) {
    									LinkedHashMap<String,String> variablesMap = processFileDialog.getVariablesMap();
    									String filename = processFileDialog.getFilename();
    									String encoding = processFileDialog.getEncoding();
    									String mimeType = processFileDialog.getMimeType();
    									processFileDialog.close();
    									if (processFileDialog.getReturnCode() == Window.OK) {
    										(new ProcessFileThruTransformerAction(
    												(XObjectEditor)TransformerMainPage.this.getEditor(),
    												variablesMap,
    												filename,
    												mimeType,
    												encoding
    										)).run();
    										
    									}
    								}
    							}
	    					);
	    					processFileDialog.setBlockOnOpen(true);
	    					processFileDialog.open();	    					
	            		} catch (Exception ex) {
	            			ex.printStackTrace();
	            		}
	            	};
	            });
                        
            
            //make the Page window a DropTarget - we need to dispose it
            windowTarget = new DropTarget(this.getPartControl(), DND.DROP_MOVE);
            windowTarget.setTransfer(new Transfer[]{TextTransfer.getInstance()});
            windowTarget.addDropListener(new DCDropTargetListener());
            
            //Sequence
            Composite sequenceGroup = this.getNewSectionComposite("Steps Sequence");
            sequenceGroup.setLayout(new GridLayout(1,false));

            Composite sequenceComposite = toolkit.createComposite(sequenceGroup,SWT.NONE);
            sequenceComposite.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            sequenceComposite.setLayout(new GridLayout(6,false));
            

            Label l3 = toolkit.createLabel(sequenceComposite, "Step Description", SWT.NULL);
            l3.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true,1,1));
          
            stepText = toolkit.createText(sequenceComposite, "",SWT.BORDER|SWT.SINGLE);
            stepText.setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,true,4,1)
            );


         
            Button addStepButton = toolkit.createButton(sequenceComposite,"Add",SWT.PUSH | SWT.TRAIL);
            addStepButton.setLayoutData(
                    new GridData(SWT.CENTER,SWT.FILL,false,true,1,1)
            );
            addStepButton.addSelectionListener(new SelectionListener() {
            	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
            	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            		//commit as we go
            		try {
            				if(stepText.getText().trim().length()==0) return;
		            		TransformerMainPage.this.comitting= true;

			           		TransformerMainPage.this.stepsList.add(

			           				TransformerMainPage.this.stepText.getText()

			           		);
		            		WSTransformerV2 wsTransformer = (WSTransformerV2)getXObject().getWsObject();
		            		ArrayList<WSTransformerProcessStep> list = new ArrayList<WSTransformerProcessStep>();
		            		if (wsTransformer.getProcessSteps() != null) { 
			            		list = new ArrayList<WSTransformerProcessStep>(
			            				Arrays.asList(wsTransformer.getProcessSteps())
			            		);
		            		}
		            		list.add(new WSTransformerProcessStep(
		            				"",
		            				TransformerMainPage.this.stepText.getText(),
		            				"",
		            				new WSTransformerVariablesMapping[0],
		            				new WSTransformerVariablesMapping[0],		            				
		            				false
		            		));
		            		
		            		wsTransformer.setProcessSteps(list.toArray(new WSTransformerProcessStep[list.size()]));
		            		TransformerMainPage.this.comitting= false;
		            		int index = TransformerMainPage.this.stepsList.getItemCount()-1;
		        			TransformerMainPage.this.stepsList.select(index);
		        			refreshStep(index);
		        			TransformerMainPage.this.stepsList.forceFocus();
		            		markDirty();
            		} catch (Exception ex) {
            			ex.printStackTrace();
            		}
            	};
            });
            
            stepsList = new List(sequenceComposite,SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
            stepsList.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,5,1)
            );
            ((GridData)stepsList.getLayoutData()).heightHint = 40;
            DragSource stepsSource = new DragSource(stepsList,DND.DROP_MOVE);
            stepsSource.setTransfer(new Transfer[]{TextTransfer.getInstance()});
            stepsSource.addDragListener(new DCDragSourceListener());
            
            stepsList.addSelectionListener(new SelectionListener() {
            	public void widgetDefaultSelected(SelectionEvent e) {widgetSelected(e);}
            	public void widgetSelected(SelectionEvent e) {
            		int index = stepsList.getSelectionIndex();
            		currentPlugin = index;
            		if (index>=0) {
            			refreshStep(index);
            		}
            	}
            });
            stepsList.addMouseListener(new MouseListener() {
            	public void mouseDoubleClick(MouseEvent e) {
            		int index = stepsList.getSelectionIndex();
            		currentPlugin = index;
            		//removeStep(index);
            	}
            	public void mouseUp(MouseEvent e) {}
            	public void mouseDown(MouseEvent e) {
            		/*
            		int index = stepsList.getSelectionIndex();
            		currentPlugin = index;
            		if (index>=0) {
            			refreshStep(index);
            		}
            		*/
            	}
            });

            
            stepsList.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e) {}
				public void keyReleased(KeyEvent e) {
					if ((e.stateMask==0) && (e.character == SWT.DEL) && (TransformerMainPage.this.stepsList.getSelectionIndex()>=0)) {
						int index = TransformerMainPage.this.stepsList.getSelectionIndex();
						removeStep(index);
					}
				}
            });

            
            Composite stepUpDownComposite = toolkit.createComposite(sequenceComposite,SWT.NONE);
            stepUpDownComposite.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
            );
            stepUpDownComposite.setLayout(new GridLayout(1,false));
            
            Button stepUpButton = toolkit.createButton(stepUpDownComposite,"Up",SWT.PUSH | SWT.CENTER);
            stepUpButton.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
            );
            stepUpButton.addSelectionListener(new SelectionListener() {
            	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
            	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            		int index =TransformerMainPage.this.stepsList.getSelectionIndex();
            		if (index>0) {
						//commit as we go
						TransformerMainPage.this.comitting= true;
            			String val = TransformerMainPage.this.stepsList.getItem(index);
            			TransformerMainPage.this.stepsList.remove(index);
            			TransformerMainPage.this.stepsList.add(val, index-1);
            			TransformerMainPage.this.stepsList.select(index-1);
            			TransformerMainPage.this.stepsList.forceFocus();
            			WSTransformerV2 wsTransformer = (WSTransformerV2)getXObject().getWsObject(); 
	            		ArrayList<WSTransformerProcessStep> list = new ArrayList<WSTransformerProcessStep>(
	            				Arrays.asList(wsTransformer.getProcessSteps())
	            		);
	            		WSTransformerProcessStep spec = list.get(index);
	            		list.remove(index);
	            		list.add(index-1, spec);
	            		wsTransformer.setProcessSteps(list.toArray(new WSTransformerProcessStep[list.size()]));
	            		TransformerMainPage.this.comitting= false;
	            		markDirty();
            		}
            	};
            });
            Button stepDownButton = toolkit.createButton(stepUpDownComposite,"Down",SWT.PUSH | SWT.CENTER);
            stepDownButton.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
            );
            stepDownButton.addSelectionListener(new SelectionListener() {
            	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
            	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            		int index =TransformerMainPage.this.stepsList.getSelectionIndex();
            		if ((index>=0) && (index < TransformerMainPage.this.stepsList.getItemCount()-1)) {
						//commit as we go
						TransformerMainPage.this.comitting= true;
            			String val = TransformerMainPage.this.stepsList.getItem(index);
            			TransformerMainPage.this.stepsList.remove(index);
            			TransformerMainPage.this.stepsList.add(val, index+1);
            			TransformerMainPage.this.stepsList.select(index+1);
            			TransformerMainPage.this.stepsList.forceFocus();
            			WSTransformerV2 wsTransformer = (WSTransformerV2)getXObject().getWsObject(); 
	            		ArrayList<WSTransformerProcessStep> list = new ArrayList<WSTransformerProcessStep>(
	            				Arrays.asList(wsTransformer.getProcessSteps())
	            		);
	            		WSTransformerProcessStep spec = list.get(index);
	            		list.remove(index);
	            		list.add(index+1, spec);
	            		wsTransformer.setProcessSteps(list.toArray(new WSTransformerProcessStep[list.size()]));
	            		TransformerMainPage.this.comitting= false;
	            		markDirty();
            		}
            	};
            });
            Button deleteStepButton = toolkit.createButton(stepUpDownComposite,"Delete",SWT.PUSH | SWT.CENTER);
            deleteStepButton.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
            );
            deleteStepButton.addSelectionListener(new SelectionListener() {
            	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
            	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            		int index =TransformerMainPage.this.stepsList.getSelectionIndex();
            		if ((index>=0) && (index < TransformerMainPage.this.stepsList.getItemCount())) {
                		removeStep(index);
            		}
            	};
            });
    
            //Plugin Specifications
            //Sequence
            Composite specsGroup = this.getNewSectionComposite("Step Specification");
            sequenceGroup.setLayout(new GridLayout(4,false));
	        disabledButton= toolkit.createButton(specsGroup, "Disabled", SWT.CHECK);
	        disabledButton.setLayoutData(
	                new GridData(SWT.FILL,SWT.FILL,false,true,4,1)
	        );
            
	       specsComposite = toolkit.createComposite(specsGroup,SWT.NULL);
	        specsComposite.setLayoutData(
	                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
	        );
	        specsComposite.setLayout(new GridLayout(4,false));


	        disabledButton.addSelectionListener(new SelectionAdapter(){
	        	@Override
	        	public void widgetSelected(SelectionEvent e) {	        		
	        		WidgetUtils.enable(specsComposite, !disabledButton.getSelection());
	        		markDirty();
	        		if(stepsList.getSelectionIndex()>=0)
	        		transformer.getProcessSteps()[stepsList.getSelectionIndex()].setDisabled(disabledButton.getSelection());
	        	}
	        });
	        stepLabel = toolkit.createLabel(specsComposite, "", SWT.NULL);
	        stepLabel.setLayoutData(
	                new GridData(SWT.FILL,SWT.FILL,true,true,4,1)
	        );
	        FontData fd = stepLabel.getFont().getFontData()[0];
	        fd.setStyle(SWT.BOLD);
	        stepLabel.setFont(new Font(Display.getDefault(),fd));
	        

            stepWidget=new TransformerStepWidget(toolkit,specsComposite);
            stepWidget.create();
	        Group parametersGroup = new Group(specsComposite,SWT.SHADOW_NONE);
	        parametersGroup.setText("Parameters");
	        parametersGroup.setLayoutData(
	                new GridData(SWT.FILL,SWT.FILL,true,true,4,1)
	        );
	        ((GridData)parametersGroup.getLayoutData()).minimumHeight = 300;
	        parametersGroup.setLayout(new GridLayout(1,true));
	        
	        parametersTextViewer = new SourceViewer(parametersGroup, new VerticalRuler(10), SWT.V_SCROLL);
	        parametersTextViewer.getControl().setLayoutData(
	                new GridData(SWT.FILL,SWT.FILL,true,true,4,1)
	        );
	        
	        parametersTextViewer.addTextListener(new ITextListener() {
	        	public void textChanged(TextEvent event) {
	        		if (refreshing) return;
	        		if (TransformerMainPage.this.stepsList.getSelectionIndex()==-1) return;
	        		//commit as we go
	        		TransformerMainPage.this.comitting= true;
	        		((WSTransformerV2)getXObject().getWsObject()).getProcessSteps()[stepsList.getSelectionIndex()].setParameters(parametersTextViewer.getDocument().get());
	        		TransformerMainPage.this.comitting= false;
	        		markDirty();            		
	        	};
	        });          
            refreshData();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

       
    protected void refreshStep(int index) {
    	if (index < 0) {
			stepWidget.inputViewer.setInput(new ArrayList<WSTransformerVariablesMapping>());
			stepWidget.outputViewer.setInput(new ArrayList<WSTransformerVariablesMapping>());
			return;
    	}
		TransformerMainPage.this.refreshing = true;
		
		stepLabel.setText(transformer.getProcessSteps()[index].getDescription());
		
		String jndi = transformer.getProcessSteps()[index].getPluginJNDI().replaceAll(TRANSFORMER_PLUGIN, "");
		pluginsCombo.setText(jndi);
		currentPluginName=jndi;
		pluginDescription.setText(pluginDescriptions.get(jndi)== null ? "" : pluginDescriptions.get(jndi));
		stepText.setText("");
		parametersTextViewer.setDocument(
				new Document(
						transformer.getProcessSteps()[index].getParameters()
				)
		);
		stepWidget.setProcessStep(transformer.getProcessSteps()[index]);
		disabledButton.setSelection(transformer.getProcessSteps()[index].getDisabled());
		WidgetUtils.enable(specsComposite, !disabledButton.getSelection());
		
		TransformerMainPage.this.refreshing = false;
    }
    
    
    protected void removeStep(int index) {
		WSTransformerV2 wsTransformer = transformer;

		//clean up boxes at the bottom

		pluginsCombo.setText("");
		pluginsCombo.select(-1);
		
		parametersTextViewer.setDocument(
				new Document("")
		);

		TransformerMainPage.this.comitting= true;
		TransformerMainPage.this.stepsList.remove(index);
		TransformerMainPage.this.stepsList.select(index-1);
		refreshStep(stepsList.getSelectionIndex());
		TransformerMainPage.this.stepsList.forceFocus();
		
		//commit as we go		
		ArrayList<WSTransformerProcessStep> list = new ArrayList<WSTransformerProcessStep>(
				Arrays.asList(wsTransformer.getProcessSteps())
		);
		list.remove(index);
		currentPlugin = -1;
		wsTransformer.setProcessSteps(list.toArray(new WSTransformerProcessStep[list.size()]));
		TransformerMainPage.this.comitting= false;
		markDirty();
    }
    

	protected void refreshData() {
		try {
//			System.out.println("refreshData() ");
			if (this.comitting) return;
			
			this.refreshing = true;
			
			WSTransformerV2 wsTransformer = (WSTransformerV2) (getXObject().getWsObject());    	
			
			descriptionText.setText(wsTransformer.getDescription() == null ? "" : wsTransformer.getDescription());
			
			stepsList.removeAll();
			WSTransformerProcessStep[] specs =  wsTransformer.getProcessSteps();
			if (specs != null) {
				for (int i = 0; i < specs.length; i++) {
					stepsList.add(
	           				specs[i].getDescription()						
					);	
				}
			}

			stepsList.select(currentPlugin);
			
			refreshStep(stepsList.getSelectionIndex());

            this.refreshing = false;

		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(this.getSite().getShell(), "Error refreshing the page", "Error refreshing the page: "+e.getLocalizedMessage());
		}    	
	}
	
	protected void commit() { 
		//changes are committed as we go
	}

	protected void createActions() {
	}


	public void textChanged(TextEvent event) {
		markDirty();
	}

	public void dispose() {
		super.dispose();
		windowTarget.dispose();
	}
	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		stepsList.setSelection(currentPlugin);
	}
	
	class TransformerStepWidget {

		FormToolkit toolkit;
		Composite parent;
		
		
		CCombo inputVariables;
		CCombo inputParams;
		
		CCombo outputParams;
		CCombo outputVariables;
		private Composite mainComposite;
		
		private Button inputLinkButton;
		private Button outputLinkButton;
		
		WSTransformerProcessStep processStep;
		private TableViewer inputViewer;
		private TableViewer outputViewer;
		
		Set<String> availableVariables=new HashSet<String>();
		public TransformerStepWidget(FormToolkit toolkit,Composite parent){
			this.toolkit=toolkit;
			this.parent=parent;

		}
		
		public WSTransformerProcessStep getProcessStep() {
			return processStep;
		}

		public void setProcessStep(WSTransformerProcessStep processStep) {
			this.processStep = processStep;
			refreshViewers();
			refreshCombo();
		}

		private TableViewer createViewer(final java.util.List<String> columns,Composite parent, final boolean isInput){
	        Table table =new Table(parent,SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER|SWT.FULL_SELECTION);
	        final TableViewer viewer = new TableViewer(table);
	        table.setToolTipText("Press 'DEL' key to delete the selected item.");
	        table.setLayoutData(    
	                new GridData(SWT.FILL,SWT.FILL,true,true,3,1)
	        );
	        ((GridData)viewer.getControl().getLayoutData()).heightHint=60;
	        
	        //table.setLayoutData(new GridData(GridData.FILL_BOTH));
	        for(String column:columns){
	        	TableColumn tableColumn=new TableColumn(table, SWT.CENTER);
	        	tableColumn.setText(column);
	        	tableColumn.setWidth(200);
	        	tableColumn.pack();
	        }  
	        table.setHeaderVisible(true);
	        table.setLinesVisible(true);

	           
	        //set the content provider
	        viewer.setContentProvider(new IStructuredContentProvider() {
	        	public void dispose() {}
	        	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
	        	public Object[] getElements(Object inputElement) {
	        		if(inputElement==null)return null;
	        		java.util.List<WSTransformerVariablesMapping> lines = (java.util.List<WSTransformerVariablesMapping>)inputElement;
	        		return lines.toArray(new WSTransformerVariablesMapping[lines.size()]);
	        	}
	        });
	        
	        //set the label provider
	        viewer.setLabelProvider(new ITableLabelProvider() {
	        	public boolean isLabelProperty(Object element, String property) {return false;}
	        	public void dispose() {}
	        	public void addListener(ILabelProviderListener listener) {}
	        	public void removeListener(ILabelProviderListener listener) {}
	        	public String getColumnText(Object element, int columnIndex) {
	        		WSTransformerVariablesMapping line = (WSTransformerVariablesMapping) element;
	        		switch(columnIndex){
	        		
	        		case 0:
	        			return isInput?line.getPipelineVariable():line.getPluginVariable();
	        		case 1:
	        			return isInput?line.getPluginVariable():line.getPipelineVariable();
	        				
	        		}
	        		return "";
	        	}
	        	public Image getColumnImage(Object element, int columnIndex) {return null;}
	        });

	        // Set the column properties
	        viewer.setColumnProperties(columns.toArray(new String[columns.size()]));
	    
	        //display for Delete Key events to delete an instance
	        viewer.getTable().addKeyListener(new KeyListener() {
	        	public void keyPressed(KeyEvent e) {}
	        	public void keyReleased(KeyEvent e) {
	        		if ((e.stateMask==0) && (e.character == SWT.DEL) && (viewer.getSelection()!=null)) {
	        			WSTransformerVariablesMapping line = (WSTransformerVariablesMapping)((IStructuredSelection)viewer.getSelection()).getFirstElement();
	        			java.util.List<WSTransformerVariablesMapping> items=(java.util.List<WSTransformerVariablesMapping>)viewer.getInput();
	        			items.remove(line);
	        			
	        			if(isInput)
	        				processStep.setInputMappings(items.toArray(new WSTransformerVariablesMapping[items.size()]));
	        			else
	        				processStep.setOutputMappings(items.toArray(new WSTransformerVariablesMapping[items.size()]));
	        			//refresh
	        			viewer.refresh();
	        			//mark for update
	        			markDirty();
	        		}
	        	}
	        });		
	        return viewer;
		}
		void refreshViewers(){
			if(processStep!=null){
				java.util.List<WSTransformerVariablesMapping> items=new ArrayList<WSTransformerVariablesMapping>();
				for(WSTransformerVariablesMapping map: processStep.getInputMappings()){
					items.add(map);
				}
				inputViewer.setInput(items);
				
				items=new ArrayList<WSTransformerVariablesMapping>();
				for(WSTransformerVariablesMapping map: processStep.getOutputMappings()){
					items.add(map);
				}
				outputViewer.setInput(items);
				
			}
		}
		void refreshCombo(){
			inputParams.removeAll();
			java.util.List<String> list=inputVariablesMap.get(pluginsCombo.getText().trim());
			if(list!=null){
				inputParams.setItems(list.toArray(new String[list.size()]));
			}
			outputParams.removeAll();
			list=outputVariablesMap.get(pluginsCombo.getText().trim());
			if(list!=null){
				outputParams.setItems(list.toArray(new String[list.size()]));
			}

			inputVariables.setItems(availableVariables.toArray(new String[availableVariables.size()]));
			outputVariables.setItems(availableVariables.toArray(new String[availableVariables.size()]));
		}
		private boolean isExist(java.util.List<WSTransformerVariablesMapping> list, String parameterName){
			for(WSTransformerVariablesMapping map: list){
				if(map.getPluginVariable().equals(parameterName)){
					MessageDialog.openInformation(null, "Warning", parameterName+" already Exists!");
					return true;
				}
			}
			return false;
		}
		private void createInput(){
			Composite inputComposite= toolkit.createComposite(mainComposite,SWT.BORDER);
			inputComposite.setLayoutData(
	                new GridData(SWT.FILL,SWT.RIGHT,true,true,1,1)
	        );		
			inputComposite.setLayout(new GridLayout(3,false));
			
			LabelCombo inputV=new LabelCombo(toolkit,inputComposite,"Input Variables",SWT.BORDER,1);		
			inputVariables=inputV.getCombo();
			
	        inputLinkButton = toolkit.createButton(inputComposite,"Link",SWT.PUSH | SWT.CENTER);
	        inputLinkButton.setToolTipText("Add a link for Input Variables and Transformer Plugin's Input Parameters");
	        inputLinkButton.setLayoutData(
	                new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
	        );
	        inputLinkButton.addSelectionListener(new SelectionAdapter(){
	        	@Override
	        	public void widgetSelected(SelectionEvent e) {
	        		if(inputParams.getText().length()==0) return;
        			java.util.List<WSTransformerVariablesMapping> items=(java.util.List<WSTransformerVariablesMapping>)inputViewer.getInput();
	        		if(isExist(items, inputParams.getText())) return;
	        		
        			WSTransformerVariablesMapping line = new WSTransformerVariablesMapping();
        			if(inputVariables.getText().trim().length()>0)
        				line.setPipelineVariable(inputVariables.getText());
        			if(inputParams.getText().trim().length()>0)
        				line.setPluginVariable(inputParams.getText());
        			
        			items.add(line);
        			processStep.setInputMappings(items.toArray(new WSTransformerVariablesMapping[items.size()]));
        			inputViewer.refresh();
        			availableVariables.add(inputVariables.getText());
        			outputVariables.setItems(availableVariables.toArray(new String[availableVariables.size()]));
        			markDirty();
	        	}
	        });
			LabelCombo inputP=new LabelCombo(toolkit,inputComposite,"Input Parameters",SWT.BORDER|SWT.READ_ONLY,1);		
			inputParams=inputP.getCombo();
			
			//create table
			java.util.List<String> columns=new ArrayList<String>();
			columns.add("Input Variables");
			columns.add("Input Parameters");
			inputViewer=createViewer(columns,inputComposite,true);
		}

		private void createOutput(){
			Composite outputComposite= toolkit.createComposite(mainComposite,SWT.BORDER);
			outputComposite.setLayoutData(
	                new GridData(SWT.FILL,SWT.RIGHT,true,true,1,1)
	        );		
			outputComposite.setLayout(new GridLayout(3,false));

			LabelCombo outputP=new LabelCombo(toolkit,outputComposite,"Output Parameters",SWT.BORDER|SWT.READ_ONLY,1);		
			outputParams=outputP.getCombo();

			
	        outputLinkButton = toolkit.createButton(outputComposite,"Link",SWT.PUSH | SWT.CENTER);
	        outputLinkButton.setToolTipText("Add a link for output Variables and Transformer Plugin's output Parameters");
	        outputLinkButton.setLayoutData(
	                new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
	        );
	        outputLinkButton.addSelectionListener(new SelectionAdapter(){
	        	@Override
	        	public void widgetSelected(SelectionEvent e) {
	        		if(outputParams.getText().length()==0) return;
        			java.util.List<WSTransformerVariablesMapping> items=(java.util.List<WSTransformerVariablesMapping>)outputViewer.getInput();
	        		if(isExist(items, outputParams.getText())) return;
	        		
        			WSTransformerVariablesMapping line = new WSTransformerVariablesMapping();
        			if(outputVariables.getText().length()>0)line.setPipelineVariable(outputVariables.getText());
        			if(outputParams.getText().trim().length()>0)line.setPluginVariable(outputParams.getText());
         			items.add(line);
        			processStep.setOutputMappings(items.toArray(new WSTransformerVariablesMapping[items.size()]));
        			availableVariables.add(outputVariables.getText());
        			outputVariables.setItems(availableVariables.toArray(new String[availableVariables.size()]));
        			outputViewer.refresh();
        			markDirty();
	        	}
	        });
			LabelCombo outputV=new LabelCombo(toolkit,outputComposite,"Output Variables",SWT.BORDER,1);		
			outputVariables=outputV.getCombo();
			
			//create table
			java.util.List<String> columns=new ArrayList<String>();
			columns.add("Output Parameters");
			columns.add("Output Variables");
			outputViewer=createViewer(columns,outputComposite,false);
		}
		private void createPlugin() throws Exception{
	        Composite specsComposite = toolkit.createComposite(mainComposite,SWT.NONE);
	        specsComposite.setLayoutData(
	                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
	        );
	        specsComposite.setLayout(new GridLayout(4,false));
	        

	        pluginDescription = toolkit.createText(specsComposite, "", SWT.MULTI|SWT.WRAP);
	        pluginDescription.setEditable(false);
	        pluginDescription.setLayoutData(
	                new GridData(SWT.FILL,SWT.FILL,true,false,4,2)
	        );
	        ((GridData)pluginDescription.getLayoutData()).heightHint = 35;
	            Label jndiLabel = toolkit.createLabel(specsComposite, "Plugin name", SWT.NULL);
	            jndiLabel.setLayoutData(
	                    new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
	            );
	            pluginsCombo = new CCombo(specsComposite, SWT.DROP_DOWN|SWT.BORDER);
	            pluginsCombo.addModifyListener(new ModifyListener() {
	            	public void modifyText(ModifyEvent e) {
	            		if (TransformerMainPage.this.refreshing) return;
	            		String jndi = pluginsCombo.getText();
	            		//update the description
	            		String description = pluginDescriptions.get(jndi);
	            		pluginDescription.setText(description == null ? "" : description);
	            		if (stepsList.getSelectionIndex()==-1) return;
	            		//commit as we go
	            		//TransformerMainPage.this.comitting= true;	            		
	            		if (! jndi.contains("/")) jndi=TRANSFORMER_PLUGIN+jndi;
	            		((WSTransformerV2)getXObject().getWsObject())
	            				.getProcessSteps()[stepsList.getSelectionIndex()]
	            			                  	.setPluginJNDI(jndi);
	            		TransformerMainPage.this.comitting= false;
	            		markDirty();
	            	}	            	
	            });
	            
	            pluginsCombo.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {	
						if(pluginsCombo.getText().equals(currentPluginName)){
							return;
						}
						refreshCombo();	
						inputViewer.setInput(new ArrayList<WSTransformerVariablesMapping>());
						outputViewer.setInput(new ArrayList<WSTransformerVariablesMapping>());
					}	            	
	            });
	            //feed the combo once
	            WSTransformerPluginV2SList list = port.getTransformerPluginV2SList(new WSGetTransformerPluginV2SList("EN"));
	            
	            WSTransformerPluginV2SListItem[] items = list.getItem();
	            
	            if (items!=null) {
	            	for (int i = 0; i < items.length; i++) {	            		
						pluginDescriptions.put(items[i].getJndiName(), items[i].getDescription());
					}
	            	//get the sorted list and feed the combo
	            	Set<String> jndis = pluginDescriptions.keySet();
	            	for (Iterator<String> iterator = jndis.iterator(); iterator.hasNext(); ) {
						String jndi = iterator.next();
						pluginsCombo.add(jndi);	
						//add input variables and output variables
			            WSTransformerPluginV2Details details=port.getTransformerPluginV2Details(new WSGetTransformerPluginV2Details(
	        							jndi.contains("/") ? jndi : TRANSFORMER_PLUGIN+jndi,
	        							"en"
	        					));
			            java.util.List<String>input=new ArrayList<String>();
			            for(WSTransformerPluginV2VariableDescriptor v:details.getInputVariableDescriptors()){
			            	input.add(v.getVariableName());
			            	
			            }		
			            inputVariablesMap.put(jndi, input);
			            
			            java.util.List<String>output=new ArrayList<String>();
			            for(WSTransformerPluginV2VariableDescriptor v:details.getOutputVariableDescriptors()){
			            	output.add(v.getVariableName());
			            }		
			            outputVariablesMap.put(jndi, output);		            
					}
	            }
	        Button detailsButton = toolkit.createButton(specsComposite,"Help",SWT.PUSH | SWT.CENTER);
	        detailsButton.setLayoutData(
	                new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
	        );
	        detailsButton.addSelectionListener(new SelectionListener() {
	        	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
	        	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
	        		try {
	        			String jndi;

	            			jndi = pluginsCombo.getText();
	            		if(jndi.length()==0)return;

	        			WSTransformerPluginV2Details details =port.getTransformerPluginV2Details(
	        					new WSGetTransformerPluginV2Details(
	        							jndi.contains("/") ? jndi : TRANSFORMER_PLUGIN+jndi,
	        							"en"
	        					)
	        			);
	        			final PluginDetailsDialog dialog = new PluginDetailsDialog(
	        					getSite().getShell(),
	        					details.getDescription(),
	        					details.getDocumentation(),
	        					details.getParametersSchema()
	        			);
	        			dialog.addListener(new Listener() {
	        				public void handleEvent(Event event) {dialog.close();}
	        			});
	        			
	        			dialog.setBlockOnOpen(true);
	        			dialog.open();
	        			
	        		} catch (Exception ex) {
	        			String jndi;

	            		jndi = pluginsCombo.getText();

	        			MessageDialog.openError(getSite().getShell(), "Check "+jndi, "The plugin \""+jndi+"\" did NOT respond correctly");
	        			return;
	        		}      		
	        	}
	        });
	        

		}

		public void create() throws Exception{
			mainComposite= toolkit.createComposite(parent,SWT.BORDER);
			mainComposite.setLayoutData(
	                new GridData(SWT.FILL,SWT.RIGHT,true,true,4,1)
	        );		
			mainComposite.setLayout(new GridLayout(3,false));	
			
			createInput();
			createPlugin();				
			createOutput();
		}
	}

}
