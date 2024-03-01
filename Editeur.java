package moto_6809;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.event.ActionEvent;

public class Editeur extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private int idrom = 0;
	private String pc;
	private String instru_pc;
	private int i = 0, k = 0, rows, rowu, pos;
	private Boolean cmp = false;

	JButton exec = new JButton("executer");
	JButton Pas_a_Pas = new JButton("Pas_à_Pas");
	JButton new_prog = new JButton("new");
	ArrayList<String> etiquette = new ArrayList<String>();
	
	final JTextArea textArea = new JTextArea();
	private Highlighter highlighter;
    private Highlighter.HighlightPainter painter;

	public Editeur() {
		setTitle("EDITEUR");
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(1000, 120, 250, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textArea.setBounds(10, 60, 220, 200);
		contentPane.add(textArea);
		
		highlighter = textArea.getHighlighter();
        painter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
		
		JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(10, 60, 220, 200); // Adjust the bounds as needed
        contentPane.add(scrollPane);
        
        new_prog.addActionListener(new ActionListener() { // Button to clear all
			public void actionPerformed(ActionEvent click) {
				ved_all();
				exec.setEnabled(true);
				Pas_a_Pas.setEnabled(true);	
            }
		});
		new_prog.setBounds(10, 5, 220, 23);
		contentPane.add(new_prog);
		
		Pas_a_Pas.addActionListener(new ActionListener() { // Button of pas a pas
			public void actionPerformed(ActionEvent click) {
				String Asmbl = textArea.getText();
				if( !cmp ) {
					find_all_etq(Asmbl);
					cmp = !cmp;
				}
				highlightLine(i);
				TraiteLanguageAsmblr_PasAPas(Asmbl);
            }
		});
		Pas_a_Pas.setBounds(10, 30, 110, 23);
		contentPane.add(Pas_a_Pas);
		
		exec.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String Asmbl = textArea.getText();
				if( !cmp ) {
					find_all_etq(Asmbl);
					cmp = !cmp;
				}
				TraiteLanguageAsmblr(Asmbl);
			}
		});
		exec.setBounds(120, 30, 110, 23);
		contentPane.add(exec);
	}
	
	private void afficherErreur(String message) {
		new_prog.doClick();
	    JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
	}


	protected void TraiteLanguageAsmblr_PasAPas(String asmbl) {
		try {
			String[] ligne = asmbl.split("\\n");
		    instru_pc = ligne[i];
		    String[] mots = ligne[i].split("\\s+");
		    executor(mots);
	        //System.out.println("ligne i: "+i++);
		    i++;
		} catch (Exception e) {
		    e.printStackTrace();
		    afficherErreur("Votre programme comporte des erreurs.");
		}
	}

	protected void TraiteLanguageAsmblr(String asmbl) {
		try {
			String[] ligne = asmbl.split("\\n");
		    while (!ligne[i].equals("END")) {
		    	instru_pc = ligne[i];
			    String[] mots = ligne[i].split("\\s+");
			    executor(mots);
			    highlightLine(i);
			    i++;
	        }
		    endProgram();
		    highlightLine(i);
		    Programme.displayInstructions(idrom, "END");
		    } catch (Exception e) {
		    e.printStackTrace();
		    afficherErreur("Votre programme comporte des erreurs.");
		}
	}
	
	private void highlightLine(final int number) {
	    SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    try {
			        int start, end;
			        if (i == 0) {
			            start = textArea.getLineStartOffset(number);
			            end = textArea.getLineEndOffset(number);
			        } else {
			            start = textArea.getLineStartOffset(number);
			            end = textArea.getLineEndOffset(number);
			            highlighter.removeAllHighlights();
			        }
			        textArea.setSelectionStart(start);
			        textArea.setSelectionEnd(end);
			        highlighter.addHighlight(start, end, painter);
			    } catch (BadLocationException e) {
			        e.printStackTrace();
			        afficherErreur("Votre programme comporte des erreurs.");
			    }
			}
		});
	}
	
	protected void ved_all() {
		Programme.h = 0;
		i = 0;
		cmp= false;
		highlighter.removeAllHighlights();
		for (int j = 0; j < idrom+1; j++) {
			ROM.setStringToColumn1(j, "FF");
		}
		for (int j = 0; j < 1024; j++) {
			RAM.setData("00", j);
		}
		ArchitecteInterne.resetValues();
		Programme.deleteLines(k);
		idrom = 0;
		pos = 0;
}

	private void endProgram() {
		pc = ROM.getValueOfColumn0(idrom);
		ArchitecteInterne.setPC(pc);
		ArchitecteInterne.setinstru_PC("END");
		ROM.setStringToColumn1(idrom, "3F");
		exec.setEnabled(false);
		Pas_a_Pas.setEnabled(false);		
	}

	private void executor(String[] mots) {
		int taille = mots[0].length();
		char coment = mots[0].charAt(0);
		char etiquette = mots[0].charAt(taille - 1);
		if (coment != ';' && etiquette != ':') {
			pc = ROM.getValueOfColumn0(idrom);
			ArchitecteInterne.setPC(pc);
			ArchitecteInterne.setinstru_PC(instru_pc);
			Programme.displayInstructions(idrom, instru_pc);
			k++;
			String instr = mots[0].substring(0, taille-1); //-1 si reg est un char
			char registre = mots[0].charAt(taille-1);
			if (instr.equals("LD")) {
		        instr_ld(registre, mots);
	        } else if (instr.equals("ST")) {
	        	instr_st(registre, mots);
	        } else if (instr.equals("ADD")) {
	        	instr_add(registre, mots);
	        } else if (instr.equals("SUB")) {
	    		instr_sub(registre, mots); 
	    	} else if (instr.equals("CLR")) {
	    		instr_clr(registre);
	    	} else if (instr.equals("INC")) {
	    		instr_inc(registre);
	    	} else if (instr.equals("DEC")) {
	    		instr_dec(registre);
	    	} else if (instr.equals("ASL") || instr.equals("LSL")) {
	    		instr_asl(registre);
	    	} else if (instr.equals("ASR") || instr.equals("LSR")) {
	    		instr_asr(registre);
	    	} else if (instr.equals("AND")) {
				instr_and(registre, mots); 
			} else if (instr.equals("OR")) {
				instr_or(registre, mots); 
			} else if (instr.equals("PUL")) {
				instr_pul(registre, mots); 
			} else if (instr.equals("PSH")) {
				instr_psh(registre, mots); 
			} else if (instr.equals("CMP")) {
				instr_cmp(registre, mots); 
			} else {
	    		instr = mots[0];
	    		if (instr.equals("END")) {
	    			endProgram();
	        	} else if (instr.equals("ORG")) {
	    			instr_org(mots[1]); 
	    		} else if (instr.equals("SWI")) {
	    			instr_swi(); 
	    		} else if (instr.equals("NOP")) {
	    			instr_nop(); 
	    		} else if (instr.equals("TFR")) {
	    			instr_tfr(mots); 
	    		} else if (instr.equals("EXG")) {
	    			instr_exg(mots); 
	    		} else if (instr.equals("CLR")) {
	    			instr_clrR(mots); 
	    		} else if (instr.equals("DEC")) {
	    			instr_decR(mots[1]); 
	    		} else if (instr.equals("INC")) {
	    			instr_incR(mots[1]); 
	    		} else if (instr.equals("MUL")) {
	    			instr_mul(); 
	    		} else if (instr.equals("ABX")) {
	    			instr_abx(); 
	    		}if(instr.equals("JMP")) {
					goto_etiquette(mots[1]);
				}
				else if(mots[0].charAt(0) == 'B') { 
					if(instr.equals("BRA")) {
						goto_etiquette(mots[1]);
					} else if (instr.equals("BCC")) {// Branchement si pas de retenue
						if (ArchitecteInterne.getC().equals("0")) {
							   goto_etiquette(mots[1]);
						   }
					} else if (instr.equals("BCS")) {// Branchement si retenue
						   if (ArchitecteInterne.getC().equals("1")) {
							   goto_etiquette(mots[1]);
						   }
				    } else if (instr.equals("BEQ")) {// Branchement si égale à zéro
						   if (ArchitecteInterne.getZ().equals("1")) {
							   goto_etiquette(mots[1]);
						   }
				    } else if (instr.equals("BNE")) {// Branchement si différent de zéro
						   if (ArchitecteInterne.getZ().equals("0")) {
							   goto_etiquette(mots[1]);
							   }
				    } else if (instr.equals("BGE")) {// Branchement si supérieur ou égal à zéro
						   if (ArchitecteInterne.getN().equals("0") || ArchitecteInterne.getZ().equals("1")) {
							   goto_etiquette(mots[1]);
						   }
				    } else if (instr.equals("BLT")) {// Branchement si inférieur (signé)
						   if (ArchitecteInterne.getN().equals("1")) {
							   goto_etiquette(mots[1]);
						   }
				    } else if (instr.equals("BGT")) {// Branchement si supérieur (signé)
						   if (ArchitecteInterne.getN().equals("0") && ArchitecteInterne.getZ().equals("0")) {
							   goto_etiquette(mots[1]);
						   }
				    } else if (instr.equals("BLE")) {// Branchement si inférieur ou égal (signé)
						   if (ArchitecteInterne.getN().equals("1") || ArchitecteInterne.getZ().equals("1")) {
							   goto_etiquette(mots[1]);
						   }
				    } else if (instr.equals("BHI")) {// Branchement si supérieur (non signé)
						   if (ArchitecteInterne.getN().equals("0") && ArchitecteInterne.getZ().equals("0")) {
							   goto_etiquette(mots[1]);
						   }
				    } else if (instr.equals("BLS")) {// Branchement si inférieur ou égal (non signé)
						   if (ArchitecteInterne.getN().equals("1") || ArchitecteInterne.getZ().equals("1")) {
							   goto_etiquette(mots[1]);
						   }
				    } else if (instr.equals("BMI")) {// Branchement si négatif
						   if (ArchitecteInterne.getN().equals("1")) {
							goto_etiquette(mots[1]);
						   }
				    } else if (instr.equals("BPL")) {// Branchement si positif
						   if (ArchitecteInterne.getN().equals("0") || ArchitecteInterne.getZ().equals("1")) {
							   goto_etiquette(mots[1]);
						   }
				    } else if (instr.equals("BVC")) {// Branchement si pas de débordement
						   if (ArchitecteInterne.getV().equals("0")) {
							   goto_etiquette(mots[1]);
						   }
				    } else if (instr.equals("BVS")) {// Branchement si débordement
						   if (ArchitecteInterne.getV().equals("1")) {
							goto_etiquette(mots[1]);
							}
					}
				}
			}
		} else {
			Programme.displayInstructions(instru_pc);
			k++;
		}
	}

	private void instr_org(String adresse) {
		int id = hexToDecimal(adresse) - 5120;
		if (id >= 0 && id <= 1024) {
			idrom = (int) (id);
		} else {
			afficherErreur("Votre programme comporte des erreurs.");
			
		}
	}

	public static int hexToDecimal(String hexString) {
        // Using parseInt with base 16 to convert hexadecimal to decimal
        int decimalValue = Integer.parseInt(hexString, 16);
        return decimalValue;
    }

	private void instr_cmp(char registre, String[] mots) {//traitement des adresses a 1 octect puis reg 2o!
		int taille= mots[1].length();						//manque traitements pour DP et PC
		int val = -127, val_reg;
		val_reg= hextoInt( getR(registre) );
		if(mots[1].charAt(0) == '#') {//mode d'adressage immediat
			val= hextoInt( mots[1].substring(2) );
			ROM.setStringToColumn1(idrom, "81");
            idrom++;
            ROM.setStringToColumn1(idrom, mots[1].substring(2));
            idrom++;
		}
		else if(mots[1].charAt(0) == '$' && mots[1].indexOf(',') < 0){
			String adresse = mots[1].substring(1);
			if(taille < 4 ) {							//adressage directe
				adresse = ArchitecteInterne.getDP() + adresse;//concatenation depui DP
			}
			val= hextoInt((String)RAM.getData(adresse));
			ROM.setStringToColumn1(idrom, "B1");
            idrom++;
            ROM.setStringToColumn1(idrom, mots[1].substring(1));
            idrom++;
		}else {							//adressage indexé
			String adresse = null, deplacement = null;
			char reg2 = 0, reg3;
			if(mots[1].charAt(0) == ',') {
				String tmps;
				int tmpi = 0;
				if(taille == 3) {
					if(mots[1].charAt(1) == '+'){//cas ,+X  preincrementation
						System.out.println("hello");
						reg2= mots[1].charAt(2);
						tmps= getR(reg2);
						tmpi= hextoInt(tmps);
						tmpi++;
					}
					else if(mots[1].charAt(1) == '-'){//cas ,-X
						System.out.println("hello2");
						reg2= mots[1].charAt(2);
						tmps= getR(reg2);
						tmpi= hextoInt(tmps);
						tmpi--;
					}
					tmps = intToHex(tmpi);
					setR(reg2, tmps);
					adresse = getR(reg2);//on recupere l'adresse
				}
				if(taille == 2) {//on obtient l'adresse depuis le reg cas ,X
					reg2= mots[1].charAt(1);
					adresse = getR(reg2);
				}
				//post(in/de)crementation
				if(taille == 3) {
					if(mots[1].charAt(2) == '+'){//cas ,X+
						reg2= mots[1].charAt(1);
						tmps= getR(reg2);
						tmpi= hextoInt(tmps);
						tmpi++;
					}
					else if(mots[1].charAt(2) == '-'){//cas ,X-
						reg2= mots[1].charAt(1);
						tmps= getR(reg2);
						tmpi= hextoInt(tmps);
						tmpi--;
					}
					tmps = intToHex(tmpi);
					setR(reg2, tmps);
					adresse = getR(reg2);//on recupere l'adresse
				}
				
				if(adresse.length()<3) 
					adresse = "00" + adresse;
				
				val= hextoInt((String)RAM.getData(adresse));
			}
			else if(mots[1].charAt(0) == '$'&& mots[1].indexOf(',') > 0) {
				reg2= mots[1].charAt(taille-1);
				int pos_vir= mots[1].indexOf(',');
				deplacement = mots[1].substring(1, pos_vir);
				System.out.println("le deplacement est de: "+hextoInt(deplacement)+" le registre est: "+mots[1].charAt(taille-1));
				adresse = getR(reg2);
				int tmpi= hextoInt(adresse);
				tmpi += hextoInt(deplacement);
				tmpi += hextoInt(adresse);
				adresse= intToHex(tmpi);
				val= hextoInt((String)RAM.getData(adresse));
				System.out.println("new adresse dec est de: "+hextoInt(adresse));
			}
			else if(mots[1].charAt(0) == '[') {
				if(mots[1].charAt(taille-1) == ']') {
					if(mots[1].charAt(1) == ',') {					//cas: [,X] ou [,-X] ou [,X++]...
						String tmps;
						int tmpi = 0;
						if(taille == 5) {
							if(mots[1].charAt(2) == '+'){//cas ,+X  preincrementation
								System.out.println("hello");
								reg2= mots[1].charAt(3);
								tmps= getR(reg2);
								tmpi= hextoInt(tmps);
								tmpi++;
							}
							else if(mots[1].charAt(2) == '-'){//cas ,-X
								System.out.println("hello2");
								reg2= mots[1].charAt(3);
								tmps= getR(reg2);
								tmpi= hextoInt(tmps);
								tmpi--;
							}
							tmps = intToHex(tmpi);
							setR(reg2, tmps);
							adresse = getR(reg2);//on recupere l'adresse
						}
						if(taille == 4) {//on obtient l'adresse depuis le reg cas ,X
							reg2= mots[1].charAt(2);
							adresse = getR(reg2);
						}
						//post(in/de)crementation
						if(taille == 5) {
							if(mots[1].charAt(3) == '+'){//cas [,X+]
								reg2= mots[1].charAt(2);
								tmps= getR(reg2);
								tmpi= hextoInt(tmps);
								tmpi++;
							}
							else if(mots[1].charAt(3) == '-'){//cas [,X-]
								reg2= mots[1].charAt(2);
								tmps= getR(reg2);
								tmpi= hextoInt(tmps);
								tmpi--;
							}
							tmps = intToHex(tmpi);
							setR(reg2, tmps);
							adresse = getR(reg2);//on recupere l'adresse
						}
						if(adresse.length()<3) 
							adresse = "00" + adresse;
						System.out.print("old adresse: "+adresse);
						adresse= (String)RAM.getData(adresse);			//on recupere une nouvelle adresse depuis l'adresse originale
						System.out.print("new adresse: "+adresse);
						val= hextoInt((String)RAM.getData(adresse));
					}
					else
						System.out.println("hello");
						if(mots[1].charAt(1) == '$'&& mots[1].indexOf(',') > 0) {		//cas [$20,X]
							System.out.println("hello2");
							reg2= mots[1].charAt(taille-2);
							int pos_vir= mots[1].indexOf(',');
							deplacement = mots[1].substring(2, pos_vir);
							System.out.println("le deplacement est de: "+hextoInt(deplacement)+" le registre est: "+reg2);
							adresse = getR(reg2);
							int tmpi= hextoInt(adresse);
							tmpi   += hextoInt(deplacement);
							adresse = intToHex(tmpi);
							System.out.println(adresse);
							adresse = (String)RAM.getData(adresse);	//on recupere une nouvelle adresse depuis l'adresse originale
							val= hextoInt((String)RAM.getData(adresse));
							System.out.println("new adresse dec est de: "+hextoInt(adresse));
					}
				}
			}
			else {
				int tmp_dep;
				int tmp_adr;
				if(mots[1].charAt(1) == ',' && taille < 3) {//cas A,X
					reg2= mots[1].charAt(2);//dans notre exp c'est X
					reg3= mots[1].charAt(0);//c'est A
					deplacement = getR(reg3);
					adresse = getR(reg2);
				}
				else if(mots[1].charAt(0) == '$'&& mots[1].indexOf(',') > 0){									//cas $20,X
					deplacement= mots[1].substring(1, mots[1].indexOf(','));					//verifier les substring!!
					reg2= mots[1].charAt(taille-1);
					adresse = getR(reg2);
				}
				if(deplacement != null || adresse != null) {
					tmp_dep= hextoInt(deplacement);
					tmp_adr= hextoInt(adresse);
					tmp_adr =+ tmp_dep;
					adresse= intToHex(tmp_adr);
					val= hextoInt((String)RAM.getData(adresse));
				}
			}
		}
		System.out.println(mots[1].charAt(1));
		System.out.println("we cmp : "+val_reg+" to : "+val);
		if(val_reg < val) {
			System.out.println("i'm here");
    		ArchitecteInterne.setN("1");
		}else if(val_reg == val) {
			ArchitecteInterne.setZ("1");
		}else {
			ArchitecteInterne.setN("0");
			ArchitecteInterne.setZ("0");
		}
	}

	private void instr_or(char registre, String[] mots) {
		if(mots[1].charAt(0) == '#' && mots[1].charAt(1) == '$') {		//Mode d'adressage immediat
    		String val = mots[1].substring(2);
        	switch(registre) {
        		case 'A':
        			String valA = ArchitecteInterne.getA();
        			int newa = hextoInt(valA) | hextoInt(val);
        			int sizea = intToHex(newa).length();
        			ArchitecteInterne.setA(intToHex(newa).substring(sizea-2));
        			ROM.setStringToColumn1(idrom, "8A");
                    idrom++;
                    ROM.setStringToColumn1(idrom, val);
                    idrom++;
        			break;
        		case 'B':
        			String valB = ArchitecteInterne.getA();
        			int newb = hextoInt(valB) | hextoInt(val);
        			int sizeb = intToHex(newb).length();
        			ArchitecteInterne.setA(intToHex(newb).substring(sizeb-2));
        			ROM.setStringToColumn1(idrom, "CA");
                    idrom++;
                    ROM.setStringToColumn1(idrom, val);
                    idrom++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
    	} else if (mots[1].charAt(0) == '$') {
		int size = mots[1].length();
		if (size == 5)		//Mode d'adressage etendu
		{
			String adresse = mots[1].substring(1, 5);
			String val = null;
			if (registre != 'G') {
				val = (String) RAM.getData(adresse);
			}
        	switch(registre) {
        		case 'A':
        			String valA = ArchitecteInterne.getA();
        			int newa = hextoInt(valA) | hextoInt(val);
        			int sizea = intToHex(newa).length();
        			ArchitecteInterne.setA(intToHex(newa).substring(sizea-2));
        			ROM.setStringToColumn1(idrom, "BA");
                    idrom++;
                    ROM.setStringToColumn1(idrom, adresse);
                    idrom++;
                    idrom++;
        			break;
        		case 'B':
        			String valB = ArchitecteInterne.getA();
        			int newb = hextoInt(valB) | hextoInt(val);
        			int sizeb = intToHex(newb).length();
        			ArchitecteInterne.setA(intToHex(newb).substring(sizeb-2));
        			ROM.setStringToColumn1(idrom, "FA");
                    idrom++;
                    ROM.setStringToColumn1(idrom, adresse);
                    idrom++;
                    idrom++;
        			break;
        		case 'G': // La command ORG $B000 pour declare la 1er adresse pour le programme dans la ROM
        			instr_org(adresse);
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
			
		} else if (size == 3) {		//Mode d'adressage direct
			String adresse = (String) ArchitecteInterne.getDP() + mots[1].substring(1, 3);
			String add1 = adresse.substring(2);
			String val = (String) RAM.getData(adresse);
        	switch(registre) {
        		case 'A':
        			String valA = ArchitecteInterne.getA();
        			int newa = hextoInt(valA) | hextoInt(val);
        			int sizea = intToHex(newa).length();
        			ArchitecteInterne.setA(intToHex(newa).substring(sizea-2));
        			ROM.setStringToColumn1(idrom, "9A");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add1);
                    idrom++;
        			break;
        		case 'B':
        			String valB = ArchitecteInterne.getA();
        			int newb = hextoInt(valB) | hextoInt(val);
        			int sizeb = intToHex(newb).length();
        			ArchitecteInterne.setA(intToHex(newb).substring(sizeb-2));
        			ROM.setStringToColumn1(idrom, "DA");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add1);
                    idrom++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
			}
		} 
		}
	}

	private void instr_and(char registre, String[] mots) {
		if(mots[1].charAt(0) == '#') {		//Mode d'adressage immediat
    		String val = mots[1].substring(2);
        	switch(registre) {
        		case 'A':
        			String valA = ArchitecteInterne.getA();
        			int newa = hextoInt(valA) & hextoInt(val);
        			int sizea = intToHex(newa).length();
        			ArchitecteInterne.setA(intToHex(newa).substring(sizea-2));
        			ROM.setStringToColumn1(idrom, "84");
                    idrom++;
                    ROM.setStringToColumn1(idrom, val);
                    idrom++;
        			break;
        		case 'B':
        			String valB = ArchitecteInterne.getA();
        			int newb = hextoInt(valB) & hextoInt(val);
        			int sizeb = intToHex(newb).length();
        			ArchitecteInterne.setA(intToHex(newb).substring(sizeb-2));
        			ROM.setStringToColumn1(idrom, "C4");
                    idrom++;
                    ROM.setStringToColumn1(idrom, val);
                    idrom++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
    	} else if (mots[1].charAt(0) == '$') {
		int size = mots[1].length();
		if (size == 5)		//Mode d'adressage etendu
		{
			String adresse = mots[1].substring(1, 5);
			String val = (String) RAM.getData(adresse);
        	switch(registre) {
        		case 'A':
        			String valA = ArchitecteInterne.getA();
        			int newa = hextoInt(valA) & hextoInt(val);
        			int sizea = intToHex(newa).length();
        			ArchitecteInterne.setA(intToHex(newa).substring(sizea-2));
        			ROM.setStringToColumn1(idrom, "B4");
                    idrom++;
                    ROM.setStringToColumn1(idrom, adresse);
                    idrom++;
                    idrom++;
        			break;
        		case 'B':
        			String valB = ArchitecteInterne.getA();
        			int newb = hextoInt(valB) & hextoInt(val);
        			int sizeb = intToHex(newb).length();
        			ArchitecteInterne.setA(intToHex(newb).substring(sizeb-2));
        			ROM.setStringToColumn1(idrom, "F4");
                    idrom++;
                    ROM.setStringToColumn1(idrom, adresse);
                    idrom++;
                    idrom++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
			
		} else if (size == 3) {		//Mode d'adressage direct
			String adresse = (String) ArchitecteInterne.getDP() + mots[1].substring(1, 3);
			String add1 = adresse.substring(2);
			String val = (String) RAM.getData(adresse);
        	switch(registre) {
        		case 'A':
        			String valA = ArchitecteInterne.getA();
        			int newa = hextoInt(valA) & hextoInt(val);
        			int size2 = intToHex(newa).length();
        			ArchitecteInterne.setA(intToHex(newa).substring(size2-2));
        			ROM.setStringToColumn1(idrom, "94");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add1);
                    idrom++;
        			break;
        		case 'B':
        			String valB = ArchitecteInterne.getA();
        			int newb = hextoInt(valB) & hextoInt(val);
        			int sizeb = intToHex(newb).length();
        			ArchitecteInterne.setA(intToHex(newb).substring(sizeb-2));
        			ROM.setStringToColumn1(idrom, "D4");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add1);
                    idrom++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
			}
		}
		}
	}

	private void instr_asr(char registre) {
		switch(registre) {
		case 'A':
			String valA = ArchitecteInterne.getA();
			int valintA = hextoInt(valA) >> 1;
			ArchitecteInterne.setA(intToHex(valintA).substring(2));
			String binaryStringA = Integer.toBinaryString(hextoInt(valA));
			int sizea = binaryStringA.length();
			String a = (String) binaryStringA.substring(sizea-1,sizea);
			ArchitecteInterne.setC(a);
			ROM.setStringToColumn1(idrom, "47");
            idrom++;
			break;
		case 'B':
			String valB = ArchitecteInterne.getB();
			int valintB = hextoInt(valB) >> 1;
			ArchitecteInterne.setB(intToHex(valintB).substring(2));
			String binaryStringB = Integer.toBinaryString(hextoInt(valB));
			int sizeb = binaryStringB.length();
			String b = (String) binaryStringB.substring(sizeb-1,sizeb);
			ArchitecteInterne.setC(b);
			ROM.setStringToColumn1(idrom, "57");
            idrom++;
			break;
		default:
            throw new IllegalArgumentException("Registre non pris en charge : " + registre);
	}
	}

	private void instr_asl(char registre) {
		switch(registre) {
		case 'A':
			String valA = ArchitecteInterne.getA();
			int valintA = hextoInt(valA) << 1;
			ArchitecteInterne.setA(intToHex(valintA).substring(2));
			String binaryStringA = Integer.toBinaryString(valintA);
			int sizea = binaryStringA.length();
			String a = (String) binaryStringA.substring(sizea-3,sizea-2);
			ArchitecteInterne.setC(a);
			ROM.setStringToColumn1(idrom, "48");
            idrom++;
			break;
		case 'B':
			String valB = ArchitecteInterne.getB();
			int valintB = hextoInt(valB) << 1;
			ArchitecteInterne.setB(intToHex(valintB).substring(2));
			String binaryStringB = Integer.toBinaryString(valintB);
			int sizeb = binaryStringB.length();
			String b = (String) binaryStringB.substring(sizeb-3,sizeb-2);
			ArchitecteInterne.setC(b);
			ROM.setStringToColumn1(idrom, "58");
            idrom++;
			break;
		default:
            throw new IllegalArgumentException("Registre non pris en charge : " + registre);
	}
	}

	private void instr_abx() {
		int B = hextoInt(ArchitecteInterne.getB());
		int X = hextoInt(ArchitecteInterne.get_X());
		String val = intToHex(X + B);
		ArchitecteInterne.setX(val);
		flagNeg(val,16);
		flagZero(val);
		ROM.setStringToColumn1(idrom, "3A");
        idrom++;
	}

	private void instr_incR(String mots) {
		if(mots.charAt(0) == '$') {
			int size = mots.length();
			if (size == 3) {
				String adresse = (String) ArchitecteInterne.getDP() + mots.substring(1, 3);
				String add1 = adresse.substring(2);
				String val = (String) RAM.getData(adresse);
				int new_val = hextoInt(val) + 1;
				RAM.setData(intToHex(new_val).substring(2), hextoInt(adresse));
				ROM.setStringToColumn1(idrom, "0C");
		        idrom++;
		        ROM.setStringToColumn1(idrom, add1);
		        idrom++;
			} else if (size == 5) {
				String adresse = mots.substring(1, 5);
				String add1 = adresse.substring(2);
				String add2 = adresse.substring(0, 2);
				String val = (String) RAM.getData(adresse);
				int new_val = hextoInt(val) + 1;
				RAM.setData(intToHex(new_val).substring(2), hextoInt(adresse));
				ROM.setStringToColumn1(idrom, "7C");
		        idrom++;
		        ROM.setStringToColumn1(idrom, add1);
		        idrom++;
		        ROM.setStringToColumn1(idrom, add2);
		        idrom++;
			}
		} else {
			ROM.setStringToColumn1(idrom, "6C");
	        idrom++;
	        int size = mots.length();
	        if (size == 1) {
	        	char R = mots.charAt(0);
	        	int val = hextoInt(getR(R));
	        	String new_val = intToHex(val + 1);
	        	setR(R, new_val);
	        	setROMpsh(R);
	        } else {
	        	int val = hextoInt(ArchitecteInterne.getDP());
	        	String new_val = intToHex(val + 1);
	        	ArchitecteInterne.setDP(new_val);
	        	ROM.setStringToColumn1(idrom, "08");
		        idrom++;
	        }
		}
	}

	private void instr_decR(String mots) {
		if(mots.charAt(0) == '$') {
			int size = mots.length();
			if (size == 3) {
				String adresse = (String) ArchitecteInterne.getDP() + mots.substring(1, 3);
				String add1 = adresse.substring(2);
				String val = (String) RAM.getData(adresse);
				int new_val = hextoInt(val) - 1;
				RAM.setData(intToHex(new_val).substring(2), hextoInt(adresse));
				ROM.setStringToColumn1(idrom, "0A");
		        idrom++;
		        ROM.setStringToColumn1(idrom, add1);
		        idrom++;
			} else if (size == 5) {
				String adresse = mots.substring(1, 5);
				String add1 = adresse.substring(2);
				String add2 = adresse.substring(0, 2);
				String val = (String) RAM.getData(adresse);
				int new_val = hextoInt(val) - 1;
				RAM.setData(intToHex(new_val).substring(2), hextoInt(adresse));
				ROM.setStringToColumn1(idrom, "7A");
		        idrom++;
		        ROM.setStringToColumn1(idrom, add1);
		        idrom++;
		        ROM.setStringToColumn1(idrom, add2);
		        idrom++;
			}
		} else {
			ROM.setStringToColumn1(idrom, "6A");
	        idrom++;
	        int size = mots.length();
	        if (size == 1) {
	        	char R = mots.charAt(0);
	        	int val = hextoInt(getR(R));
	        	String new_val = intToHex(val - 1);
	        	setR(R, new_val);
	        	setROMpsh(R);
	        } else {
	        	int val = hextoInt(ArchitecteInterne.getDP());
	        	String new_val = intToHex(val - 1);
	        	ArchitecteInterne.setDP(new_val);
	        	ROM.setStringToColumn1(idrom, "08");
		        idrom++;
	        }
		}
	}

	private void instr_mul() {
		int A = hextoInt(ArchitecteInterne.getA());
		int B = hextoInt(ArchitecteInterne.getB());
		int M = A * B;
		String mul = intToHex(M);
		ArchitecteInterne.setD(mul);
		flagNeg(mul,16);
		flagZero(mul);
		ROM.setStringToColumn1(idrom, "3D");
        idrom++;		
	}

	private void instr_dec(char registre) {
    	switch(registre) {
    		case 'A':
    			String valA = ArchitecteInterne.getA();
    			int valintA = hextoInt(valA) - 1;
    			String valAA = intToHex(valintA);
    			@SuppressWarnings("unused") 
    			String val = valAA.substring(2);
    			ArchitecteInterne.setA(valAA.substring(2));
    			flagNeg(valAA.substring(2),8);
    			flagZero(valAA.substring(2));
    			pc = ROM.getValueOfColumn0(idrom);
    			ArchitecteInterne.setPC(pc);
    			ArchitecteInterne.setinstru_PC(instru_pc);
    			ROM.setStringToColumn1(idrom, "4A");
                idrom++;
    			break;
    		case 'B':
    			pc = ROM.getValueOfColumn0(idrom);
    			ArchitecteInterne.setPC(pc);
    			ArchitecteInterne.setinstru_PC(instru_pc);
    			String valB = ArchitecteInterne.getB();
    			int valintB = hextoInt(valB) - 1;
    			String valBB = intToHex(valintB);
    			ArchitecteInterne.setB(valBB.substring(2));
    			flagNeg(valBB.substring(2),8);
    			flagZero(valBB.substring(2));
    			pc = ROM.getValueOfColumn0(idrom);
    			ArchitecteInterne.setPC(pc);
    			ArchitecteInterne.setinstru_PC(instru_pc);
    			ROM.setStringToColumn1(idrom, "5A");
                idrom++;
    			break;
    		default:
                throw new IllegalArgumentException("Registre non pris en charge : " + registre);
    	}
		
	}

	private void instr_clrR(String[] mots) {
		if(mots[1].charAt(0) == '$') {
			int size = mots[1].length();
			if (size == 3) {
				String adresse = (String) ArchitecteInterne.getDP() + mots[1].substring(1, 3);
				String add1 = adresse.substring(2);
				RAM.setData("00", hextoInt(adresse));
				ROM.setStringToColumn1(idrom, "0C");
		        idrom++;
		        ROM.setStringToColumn1(idrom, add1);
		        idrom++;
			} else if (size == 5) {
				String adresse = mots[1].substring(1, 5);
				String add1 = adresse.substring(2);
				String add2 = adresse.substring(0, 2);
				RAM.setData("00", hextoInt(adresse));
				ROM.setStringToColumn1(idrom, "7C");
		        idrom++;
		        ROM.setStringToColumn1(idrom, add1);
		        idrom++;
		        ROM.setStringToColumn1(idrom, add2);
		        idrom++;
			}
		} else {
			ROM.setStringToColumn1(idrom, "6C");
	        idrom++;
	        int size = mots[1].length();
	        if (size == 1) {
	        	char R = mots[1].charAt(0);
	        	setR(R, "0000");
	        	setROMpsh(R);
	        } else {
	        	ArchitecteInterne.setDP("0000");
	        	ROM.setStringToColumn1(idrom, "08");
		        idrom++;
	        }
		}
	}

	private void instr_pul(char registre, String[] mots) {
		String adresse1;
		String adresse2;
		int z;
		String val = null;
		switch(registre) {
		case 'S':
			int s = hextoInt(ArchitecteInterne.getS());
			ROM.setStringToColumn1(idrom, "35");
			idrom++;
			z = mots[1].length();
			adresse1 = intToHex(rows);
			adresse2 = intToHex(rows+1);
			rows++;
			ArchitecteInterne.setS(intToHex(s + 1));
			if (mots[1].charAt(0) != 'A' && mots[1].charAt(0) != 'B') {
				rows++;
				ArchitecteInterne.setS(intToHex(s + 1));
				val = (String) RAM.getData(adresse1) + RAM.getData(adresse2);
				if (z == 1) {
					char R = mots[1].charAt(z - 1);
					setROMpsh(R);
					setR(R, val);
				} else if (z == 2) {
					ArchitecteInterne.setDP(val);
					ROM.setStringToColumn1(idrom, "08");
					idrom++;
				}
			} else {
				char R = mots[1].charAt(0);
				setROMpsh(R);
				setR(R, (String)RAM.getData(adresse1));
			}
			break;
		case 'U':
			int u = hextoInt(ArchitecteInterne.getU());
			ROM.setStringToColumn1(idrom, "37");
			idrom++;
			z = mots[1].length();
			adresse1 = intToHex(rowu);
			adresse2 = intToHex(rowu+1);
			rowu++;
			ArchitecteInterne.setU(intToHex(u + 1));
			if (mots[1].charAt(0) != 'A' && mots[1].charAt(0) != 'B') {
				rowu++;
				ArchitecteInterne.setU(intToHex(u + 1));
				val = (String) RAM.getData(adresse1) + RAM.getData(adresse2);
				if (z == 1) {
					char R = mots[1].charAt(z - 1);
					setROMpsh(R);
					setR(R, val);
				} else if (z == 2) {
					ArchitecteInterne.setDP(val);
					ROM.setStringToColumn1(idrom, "08");
					idrom++;
				}
			} else {
				char R = mots[1].charAt(0);
				setROMpsh(R);
				setR(R, (String)RAM.getData(adresse1));
			}
			break;
		default:
            throw new IllegalArgumentException("Registre non pris en charge : " + registre);
	}
	}

	private void instr_psh(char registre, String[] mots) {
		String adresse;
		int z;
		String val = null;
		int size;
		switch(registre) {
		case 'S':
			int s = hextoInt(ArchitecteInterne.getS());
			ROM.setStringToColumn1(idrom, "34");
			idrom++;
			adresse = ArchitecteInterne.getS();
			rows = hextoInt(adresse) - 1;
			z = mots[1].length();
			if (z == 1) {
				char R = mots[1].charAt(z - 1);
				setROMpsh(R);
				val = getR(R);
			} else if (z == 2) {
				val = ArchitecteInterne.getDP();
				ROM.setStringToColumn1(idrom, "08");
				idrom++;
			} else if (z == 4) {
				val = mots[1].substring(2);
				ROM.setStringToColumn1(idrom, val);
				idrom++;
			}
			size = val.length();
			if (size == 2) {
				RAM.setData(val, rows);
				ArchitecteInterne.setS(intToHex(s - 1));
			} else if (size == 4) {
				String val1 = val.substring(2);
				String val2 = val.substring(0, 2);
				RAM.setData(val1, rows);
				ArchitecteInterne.setS(intToHex(s - 1));
				RAM.setData(val2, rows);
				ArchitecteInterne.setS(intToHex(s - 1));
			}
			break;
		case 'U':
			int u = hextoInt(ArchitecteInterne.getU());
			ROM.setStringToColumn1(idrom, "36");
			idrom++;
			adresse = ArchitecteInterne.getU();
			rowu = hextoInt(adresse) - 1;
			z = mots[1].length();
			if (z == 1) {
				char R = mots[1].charAt(z - 1);
				setROMpsh(R);
				val = getR(R);
			} else if (z == 2) {
				val = ArchitecteInterne.getDP();
				ROM.setStringToColumn1(idrom, "08");
				idrom++;
			} else if (z == 4) {
				val = mots[1].substring(2);
				ROM.setStringToColumn1(idrom, val);
				idrom++;
			}
			size = val.length();
			if (size == 2) {
				RAM.setData(val, rowu);
				ArchitecteInterne.setU(intToHex(u - 1));
			} else if (size == 4) {
				String val1 = val.substring(2);
				String val2 = val.substring(0, 2);
				RAM.setData(val1, rowu);
				ArchitecteInterne.setU(intToHex(u - 1));
				RAM.setData(val2, rowu);
				ArchitecteInterne.setU(intToHex(u - 1));
			}
			break;
		default:
            throw new IllegalArgumentException("Registre non pris en charge : " + registre);
	}
	}

	private void setROMpsh(char r) {
		switch(r) {
		case 'A':
			ROM.setStringToColumn1(idrom, "02");
			idrom++;
			break;
		case 'B':
			ROM.setStringToColumn1(idrom, "04");
			idrom++;
			break;
		case 'D':
			ROM.setStringToColumn1(idrom, "06");
			idrom++;
			break;
		case 'X':
			ROM.setStringToColumn1(idrom, "10");
			idrom++;
			break;
		case 'Y':
			ROM.setStringToColumn1(idrom, "20");
			idrom++;
			break;
		case 'U':
			ROM.setStringToColumn1(idrom, "40");
			idrom++;
			break;
		default:
            throw new IllegalArgumentException("Registre non pris en charge : " + r);
		}
	}

	private void instr_exg(String[] mots) {
		String TMP = "";
		char R1 = mots[1].charAt(0);
		char S = mots[1].charAt(1);
		if ((R1 == 'D' && S == ',') || R1 != 'D') {
			String Val = getR(R1);
			char R2 = mots[1].charAt(2);
			char F = mots[1].charAt(mots[1].length()-1);
			if (R2 != 'D' || (R2 == 'D' && F != 'P')) {
				TMP = getR(R2);
				setR(R2,Val);
				setR(R1,TMP);
			} else if (F == 'P') {
				TMP = ArchitecteInterne.getDP();
				ArchitecteInterne.setDP(Val);
				setR(R1,TMP);
			}
		}
		if (R1 == 'D' && S == 'P') {
			String Val = ArchitecteInterne.getDP();
			char R2 = mots[1].charAt(3);
			TMP = getR(R2);
			setR(R2,Val);
			ArchitecteInterne.setDP(TMP);
		}
		ROM.setStringToColumn1(idrom, "1E");
		idrom++;
		ROM.setStringToColumn1(idrom, "80");
		idrom++;
	}
	
	private void instr_tfr(String[] mots) {
		char R1 = mots[1].charAt(0);
		char S = mots[1].charAt(1);
		if ((R1 == 'D' && S == ',') || R1 != 'D') {
			String Val = getR(R1);
			char R2 = mots[1].charAt(2);
			char F = mots[1].charAt(mots[1].length()-1);
			if (R2 != 'D' || (R2 == 'D' && F != 'P')) {
				setR(R2,Val);
			} else if (F == 'P') {
				ArchitecteInterne.setDP(Val);
			}
		}
		if (R1 == 'D' && S == 'P') {
			String Val = ArchitecteInterne.getDP();
			char R2 = mots[1].charAt(3);
			setR(R2,Val);
		}
		ROM.setStringToColumn1(idrom, "1F");
		idrom++;
		ROM.setStringToColumn1(idrom, "80");
		idrom++;
	}

	private void instr_swi() {
		ROM.setStringToColumn1(idrom, "3F");
		idrom++;
		ROM.setStringToColumn1(idrom, "3F");
		exec.setEnabled(false);
	}

	private void instr_sub(char registre, String[] mots) {
		if(mots[1].charAt(0) == '#' && mots[1].charAt(1) == '$') {		//Mode d'adressage immediat
    		String val = mots[1].substring(2);
    		String val4 = mots[1].substring(2);;
    		if (mots[1].length() == 6) {
    			val4 = mots[1].substring(2, 6);
    		}
        	switch(registre) {
        		case 'A':
        			String valA = ArchitecteInterne.getA();
        			flagSUB(valA,val,8);
        			int valintA = hextoInt(valA) - hextoInt(val);
        			val = intToHex(valintA);
        			val = val.substring(2);
        			ArchitecteInterne.setA(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.setStringToColumn1(idrom, "80");
                    idrom++;
                    ROM.setStringToColumn1(idrom, val);
                    idrom++;
        			break;
        		case 'B':
        			String valB = ArchitecteInterne.getB();
        			flagSUB(valB,val,8);
        			int valintB = hextoInt(valB) - hextoInt(val);
        			val = intToHex(valintB);
        			val = val.substring(2);
        			ArchitecteInterne.setB(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.setStringToColumn1(idrom, "C0");
                    idrom++;
                    ROM.setStringToColumn1(idrom, val);
                    idrom++;
        			break;
        		case 'D':
        			String valD = ArchitecteInterne.getD();
        			flagSUB(valD,val,16);
        			int valintD = hextoInt(valD) - hextoInt(val);
        			val = intToHex(valintD);
        			ArchitecteInterne.setD(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "83");
                    idrom++;
                    ROM.setStringToColumn1(idrom, val);
                    idrom++;
                    idrom++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
    	}
		if (mots[1].charAt(0) == '$') {
		int size = mots[1].length();
		if (size == 5)		//Mode d'adressage etendu
		{
			String adresse = mots[1].substring(1, 5);
			String val = (String) RAM.getData(adresse);
			String val_next = (String) RAM.getDatapre(adresse);
			String val4 = (String) val + val_next;
        	switch(registre) {
        		case 'A':
        			String valA = ArchitecteInterne.getA();
        			flagSUB(valA,val,8);
        			int valintA = hextoInt(val) - hextoInt(valA);
        			val = intToHex(valintA);
        			val = val.substring(2);
        			ArchitecteInterne.setA(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.setStringToColumn1(idrom, "B0");
                    idrom++;
                    ROM.setStringToColumn1(idrom, adresse);
                    idrom++;
                    idrom++;
        			break;
        		case 'B':
        			String valB = ArchitecteInterne.getB();
        			flagSUB(valB,val,8);
        			int valintB = hextoInt(val) - hextoInt(valB);
        			val = intToHex(valintB);
        			val = val.substring(2);
        			ArchitecteInterne.setB(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.setStringToColumn1(idrom, "F0");
                    idrom++;
                    ROM.setStringToColumn1(idrom, adresse);
                    idrom++;
                    idrom++;
        			break;
        		case 'D':
        			String valD = ArchitecteInterne.getD();
        			flagSUB(valD,val,16);
        			int valintD = hextoInt(val) + hextoInt(valD);
        			val = intToHex(valintD);
        			ArchitecteInterne.setD(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "B3");
                    idrom++;
                    ROM.setStringToColumn1(idrom, adresse);
                    idrom++;
                    idrom++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
			
		} else if (size == 3) {		//Mode d'adressage direct
			String adresse = (String) ArchitecteInterne.getDP() + mots[1].substring(1, 3);
			String add1 = adresse.substring(2);
			String val = (String) RAM.getData(adresse);
			String val_next = (String) RAM.getDatapre(adresse);
			String val4 = (String) val + val_next;
        	switch(registre) {
        		case 'A':
        			String valA = ArchitecteInterne.getA();
        			flagSUB(valA,val,8);
        			int valintA = hextoInt(val) - hextoInt(valA);
        			val = intToHex(valintA);
        			val = val.substring(2);
        			ArchitecteInterne.setA(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.setStringToColumn1(idrom, "90");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add1);
                    idrom++;
        			break;
        		case 'B':
        			String valB = ArchitecteInterne.getB();
        			flagSUB(valB,val,8);
        			int valintB = hextoInt(val) - hextoInt(valB);
        			val = intToHex(valintB);
        			val = val.substring(2);
        			ArchitecteInterne.setB(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.setStringToColumn1(idrom, "D0");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add1);
                    idrom++;
        			break;
        		case 'D':
        			String valD = ArchitecteInterne.getD();
        			flagSUB(valD,val,16);
        			int valintD = hextoInt(val) - hextoInt(valD);
        			val = intToHex(valintD);
        			ArchitecteInterne.setD(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "93");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add1);
                    idrom++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
		}
		}
	}
	
	private void instr_nop() {
		ROM.setStringToColumn1(idrom, "12");
        idrom++;		
	}
	
	private void instr_inc(char registre) {
    	switch(registre) {
    		case 'A':
    			String valA = ArchitecteInterne.getA();
    			int valintA = hextoInt(valA) + 1;
    			String valAA = intToHex(valintA);
    			ArchitecteInterne.setA(valAA.substring(2));
    			ROM.setStringToColumn1(idrom, "4F");
                idrom++;
    			break;
    		case 'B':
    			String valB = ArchitecteInterne.getB();
    			int valintB = hextoInt(valB) + 1;
    			String valBB = intToHex(valintB);
    			ArchitecteInterne.setB(valBB.substring(2));
    			ROM.setStringToColumn1(idrom, "5F");
                idrom++;
    			break;
    		default:
                throw new IllegalArgumentException("Registre non pris en charge : " + registre);
    	}
		
	}
	
	private void instr_clr(char registre) {
	    switch(registre) {
	    	case 'A':
	    		ArchitecteInterne.setA("00");
	    		ROM.setStringToColumn1(idrom, "4F");
	        	idrom++;
	        	break;
	        case 'B':
	       		ArchitecteInterne.setB("00");
	     		ROM.setStringToColumn1(idrom, "5F");
	            idrom++;
	        	break;
	        default:
	            throw new IllegalArgumentException("Registre non pris en charge : " + registre);
	        }
				
	}

	private void instr_add(char registre, String[] mots) {
		if(mots[1].charAt(0) == '#' && mots[1].charAt(1) == '$') {		//Mode d'adressage immediat
    		String val = mots[1].substring(2);
    		String val4 = mots[1].substring(2);;
    		if (mots[1].length() == 6) {
    			val4 = mots[1].substring(2, 6);
    		}
        	switch(registre) {
        		case 'A':
        			String valA = ArchitecteInterne.getA();
        			flagADD(val,valA,8);
        			int valintA = hextoInt(val) + hextoInt(valA);
        			val = intToHex(valintA);
        			val = val.substring(2);
        			ArchitecteInterne.setA(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.setStringToColumn1(idrom, "8B");
                    idrom++;
                    ROM.setStringToColumn1(idrom, val);
                    idrom++;
        			break;
        		case 'B':
        			String valB = ArchitecteInterne.getB();
        			flagADD(val,valB,8);
        			int valintB = hextoInt(val) + hextoInt(valB);
        			val = intToHex(valintB);
        			val = val.substring(2);
        			ArchitecteInterne.setB(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.setStringToColumn1(idrom, "C4");
                    idrom++;
                    ROM.setStringToColumn1(idrom, val);
                    idrom++;
        			break;
        		case 'D':
        			String valD = ArchitecteInterne.getD();
        			flagADD(val,valD,16);
        			int valintD = hextoInt(val) + hextoInt(valD);
        			val = intToHex(valintD);
        			ArchitecteInterne.setD(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "C3");
                    idrom++;
                    ROM.setStringToColumn1(idrom, val);
                    idrom++;
                    idrom++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
    	}
		if (mots[1].charAt(0) == '$') {
		int size = mots[1].length();
		if (size == 5)		//Mode d'adressage etendu
		{
			String adresse = mots[1].substring(1, 5);
			String val = (String) RAM.getData(adresse);
			String val_next = (String) RAM.getDatapre(adresse);
			String val4 = (String) val + val_next;
        	switch(registre) {
        		case 'A':
        			String valA = ArchitecteInterne.getA();
        			flagADD(val,valA,8);
        			int valintA = hextoInt(val) + hextoInt(valA);
        			val = intToHex(valintA);
        			val = val.substring(2);
        			ArchitecteInterne.setA(val);
        			flagNeg(val, 8);
    				flagZero(val);
        			ROM.setStringToColumn1(idrom, "B4");
                    idrom++;
                    ROM.setStringToColumn1(idrom, adresse);
                    idrom++;
                    idrom++;
        			break;
        		case 'B':
        			String valB = ArchitecteInterne.getB();
        			flagADD(val,valB,8);
        			int valintB = hextoInt(val) + hextoInt(valB);
        			val = intToHex(valintB);
        			val = val.substring(2);
        			ArchitecteInterne.setB(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.setStringToColumn1(idrom, "FB");
                    idrom++;
                    ROM.setStringToColumn1(idrom, adresse);
                    idrom++;
                    idrom++;
        			break;
        		case 'D':
        			String valD = ArchitecteInterne.getD();
        			flagADD(val,valD,16);
        			int valintD = hextoInt(val) + hextoInt(valD);
        			val = intToHex(valintD);
        			ArchitecteInterne.setD(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "F3");
                    idrom++;
                    ROM.setStringToColumn1(idrom, adresse);
                    idrom++;
                    idrom++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
			
		} else if (size == 3) {		//Mode d'adressage direct
			String adresse = (String) ArchitecteInterne.getDP() + mots[1].substring(1, 3);
			String add1 = adresse.substring(2);
			String val = (String) RAM.getData(adresse);
			String val_next = (String) RAM.getDatapre(adresse);
			String val4 = (String) val + val_next;
        	switch(registre) {
        		case 'A':
        			String valA = ArchitecteInterne.getA();
        			flagADD(val,valA,8);
        			int valintA = hextoInt(val) + hextoInt(valA);
        			val = intToHex(valintA);
        			val = val.substring(2);
        			ArchitecteInterne.setA(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.setStringToColumn1(idrom, "9B");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add1);
                    idrom++;
        			break;
        		case 'B':
        			String valB = ArchitecteInterne.getB();
        			flagADD(val,valB,8);
        			int valintB = hextoInt(val) + hextoInt(valB);
        			val = intToHex(valintB);
        			val = val.substring(2);
        			ArchitecteInterne.setB(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.setStringToColumn1(idrom, "DB");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add1);
                    idrom++;
        			break;
        		case 'D':
        			String valD = ArchitecteInterne.getD();
        			flagADD(val,valD,16);
        			int valintD = hextoInt(val) + hextoInt(valD);
        			val = intToHex(valintD);
        			ArchitecteInterne.setD(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "D3");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add1);
                    idrom++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
		}
		}
	}

	private void instr_st(char registre, String[] mots) {
		if (mots[1].charAt(0) == '$') {
			int size = mots[1].length();
			if (size == 5)		//Mode d'adressage etendu
			{
				String adresse = mots[1].substring(1, 5);
	        	switch(registre) {
	        		case 'A':
	        			String valA = ArchitecteInterne.getA();
	        			int ligneA = hextoInt(adresse);
	        			RAM.setData(valA, ligneA);
	        			ROM.setStringToColumn1(idrom, "B7");
	                    idrom++;
	                    ROM.setStringToColumn1(idrom, adresse);
	                    idrom++;
	                    idrom++;
	        			break;
	        		case 'B':
	        			String valB = ArchitecteInterne.getB();
	        			int ligneB = hextoInt(adresse);
	        			RAM.setData(valB, ligneB);
	        			ROM.setStringToColumn1(idrom, "F7");
	                    idrom++;
	                    ROM.setStringToColumn1(idrom, adresse);
	                    idrom++;
	                    idrom++;
	        			break;
	        		case 'D':
	        			String valD = ArchitecteInterne.getD();
	        			int ligneD = hextoInt(adresse);
	        			RAM.setData(valD.substring(0, 2), ligneD);
	        			RAM.setData(valD.substring(2), ligneD+1);
	        			ROM.setStringToColumn1(idrom, "FD");
	                    idrom++;
	                    ROM.setStringToColumn1(idrom, adresse);
	                    idrom++;
	                    idrom++;
	        			break;
	        		case 'X':
	        			String valX = ArchitecteInterne.get_X();
	        			int ligneX = hextoInt(adresse);
	        			RAM.setData(valX.substring(0, 2), ligneX);
	        			RAM.setData(valX.substring(2), ligneX+1);
	        			ROM.setStringToColumn1(idrom, "BF");
	                    idrom++;
	                    ROM.setStringToColumn1(idrom, adresse);
	                    idrom++;
	                    idrom++;
	        			break;
	        		case 'Y':
	        			String valY = ArchitecteInterne.get_Y();
	        			int ligneY = hextoInt(adresse);
	        			RAM.setData(valY.substring(0, 2), ligneY);
	        			RAM.setData(valY.substring(2), ligneY+1);
	        			ROM.setStringToColumn1(idrom, "10");
	                    idrom++;
	        			ROM.setStringToColumn1(idrom, "BF");
	                    idrom++;
	                    ROM.setStringToColumn1(idrom, adresse);
	                    idrom++;
	                    idrom++;
	        			break;
	        		case 'S':
	        			String valS = ArchitecteInterne.getS();
	        			int ligneS = hextoInt(adresse);
	        			RAM.setData(valS.substring(0, 2), ligneS);
	        			RAM.setData(valS.substring(2), ligneS+1);
	        			ROM.setStringToColumn1(idrom, "10");
	                    idrom++;
	                    ROM.setStringToColumn1(idrom, "FF");
	                    idrom++;
	                    ROM.setStringToColumn1(idrom, adresse);
	                    idrom++;
	                    idrom++;
	        			break;
	        		case 'U':
	        			String valU = ArchitecteInterne.getU();
	        			int ligneU = hextoInt(adresse);
	        			RAM.setData(valU.substring(0, 2), ligneU);
	        			RAM.setData(valU.substring(2), ligneU+1);
	        			ROM.setStringToColumn1(idrom, "FF");
	                    idrom++;
	                    ROM.setStringToColumn1(idrom, adresse);
	                    idrom++;
	                    idrom++;
	        			break;
	        		default:
	                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
	        	}
				
			} else if (size == 3 && mots[1].charAt(0) == '$') {		//Mode d'adressage direct
				String adresse = (String) ArchitecteInterne.getDP() + mots[1].substring(1, 3);
				String add = adresse.substring(2);
				switch(registre) {
        		case 'A':
        			String valA = ArchitecteInterne.getA();
        			int ligneA = hextoInt(adresse);
        			RAM.setData(valA, ligneA);
        			ROM.setStringToColumn1(idrom, "97");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add);
                    idrom++;
        			break;
        		case 'B':
        			String valB = ArchitecteInterne.getB();
        			int ligneB = hextoInt(adresse);
        			RAM.setData(valB, ligneB);
        			ROM.setStringToColumn1(idrom, "D7");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add);
                    idrom++;
        			break;
        		case 'D':
        			String valD = ArchitecteInterne.getD();
        			int ligneD = hextoInt(adresse);
        			RAM.setData(valD.substring(0, 2), ligneD);
        			RAM.setData(valD.substring(2), ligneD+1);
        			ROM.setStringToColumn1(idrom, "DD");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add);
                    idrom++;
        			break;
        		case 'X':
        			String valX = ArchitecteInterne.get_X();
        			int ligneX = hextoInt(adresse);
        			RAM.setData(valX.substring(0, 2), ligneX);
        			RAM.setData(valX.substring(2), ligneX+1);
        			ROM.setStringToColumn1(idrom, "9F");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add);
                    idrom++;
        			break;
        		case 'Y':
        			String valY = ArchitecteInterne.get_Y();
        			int ligneY = hextoInt(adresse);
        			RAM.setData(valY.substring(0, 2), ligneY);
        			RAM.setData(valY.substring(2), ligneY+1);
        			ROM.setStringToColumn1(idrom, "10");
                    idrom++;
        			ROM.setStringToColumn1(idrom, "9F");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add);
                    idrom++;
        			break;
        		case 'S':
        			String valS = ArchitecteInterne.getS();
        			int ligneS = hextoInt(adresse);
        			RAM.setData(valS.substring(0, 2), ligneS);
        			RAM.setData(valS.substring(2), ligneS+1);
        			ROM.setStringToColumn1(idrom, "10");
                    idrom++;
                    ROM.setStringToColumn1(idrom, "DF");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add);
                    idrom++;
        			break;
        		case 'U':
        			String valU = ArchitecteInterne.getU();
        			int ligneU = hextoInt(adresse);
        			RAM.setData(valU.substring(0, 2), ligneU);
        			RAM.setData(valU.substring(2), ligneU+1);
        			ROM.setStringToColumn1(idrom, "DF");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add);
                    idrom++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
			}
		}
		else {//indexé
		
			int taille= mots[1].length(), valreg= hextoInt(getR(registre));
			String adresse = null, deplacement = null;
			char reg2 = 0, reg3;
			Boolean check = false;
			if(mots[1].charAt(0) == ',') {
				String tmps;
				int tmpi = 0;
				//(in/de)crementation de 1
				if(taille == 3 &&( (mots[1].charAt(1) == '+') || (mots[1].charAt(1) == '-') )) {
					System.out.println("hello ,+/-X");
					if(mots[1].charAt(1) == '+'){//cas ,+X  preincrementation
						System.out.println("hello");
						reg2= mots[1].charAt(2);
						tmps= getR(reg2);
						tmpi= hextoInt(tmps);
						tmpi++;
						check= true;
					}
					else if(mots[1].charAt(1) == '-'){//cas ,-X
						System.out.println("hello2");
						reg2= mots[1].charAt(2);
						tmps= getR(reg2);
						tmpi= hextoInt(tmps);
						tmpi--;
						check= true;
					}
				}
				if(taille == 2) {//on obtient l'adresse depuis le reg cas ,X
					reg2= mots[1].charAt(1);
					adresse = getR(reg2);
					if(adresse.length()<3) 
						adresse = "00" + adresse;
					
					RAM.setData(intToHex(valreg).substring(2, 4), hextoInt(adresse));
				}
				//post(in/de)crementation
				if(taille == 3 && ((mots[1].charAt(2) == '+') || (mots[1].charAt(2) == '-')) ) {
					System.out.println("hello ,X+/-");
					if(mots[1].charAt(2) == '+'){//cas ,X+
						reg2= mots[1].charAt(1);
						tmps= getR(reg2);
						tmpi= hextoInt(tmps);
						tmpi++;
					}
					else if(mots[1].charAt(2) == '-'){//cas ,X-
						reg2= mots[1].charAt(1);
						tmps= getR(reg2);
						tmpi= hextoInt(tmps);
						tmpi--;
					}
					if(check){
						
						
						RAM.setData(intToHex(valreg).substring(2, 4), hextoInt(adresse));
						tmps = intToHex(tmpi);
						setR(reg2, tmps);
						adresse = getR(reg2);//on recupere l'adresse
					}
					else {
						
						tmps = intToHex(tmpi);
						setR(reg2, tmps);
						adresse = getR(reg2);//on recupere l'adresse
						if(adresse.length()<3) 
							adresse = "00" + adresse;
						RAM.setData(intToHex(valreg).substring(2, 4), hextoInt(adresse));
					}
				}
				
				
			}
			else if(mots[1].charAt(0) == '$'&& mots[1].indexOf(',') > 0) {
				reg2= mots[1].charAt(taille-1);
				int pos_vir= mots[1].indexOf(',');
				deplacement = mots[1].substring(1, pos_vir);
				System.out.println("le deplacement est de: "+hextoInt(deplacement)+" le registre est: "+mots[1].charAt(taille-1));
				adresse = getR(reg2);
				int tmpi= hextoInt(adresse);
				tmpi += hextoInt(deplacement);
				tmpi += hextoInt(adresse);
				adresse= intToHex(tmpi);
				RAM.setData(adresse, valreg);
			}
			else if(mots[1].charAt(0) == '[') {
				if(mots[1].charAt(taille-1) == ']') {
					if(mots[1].charAt(1) == ',') {					//cas: [,X] ou [,-X] ou [,X++]...
						String tmps;
						int tmpi = 0;
						if(taille == 5) {
							if(mots[1].charAt(2) == '+'){//cas ,+X  preincrementation
								System.out.println("hello");
								reg2= mots[1].charAt(3);
								tmps= getR(reg2);
								tmpi= hextoInt(tmps);
								tmpi++;
							}
							else if(mots[1].charAt(2) == '-'){//cas ,-X
								System.out.println("hello2");
								reg2= mots[1].charAt(3);
								tmps= getR(reg2);
								tmpi= hextoInt(tmps);
								tmpi--;
							}
							tmps = intToHex(tmpi);
							setR(reg2, tmps);
							adresse = getR(reg2);//on recupere l'adresse
						}
						if(taille == 4) {//on obtient l'adresse depuis le reg cas ,X
							reg2= mots[1].charAt(2);
							adresse = getR(reg2);
						}
						//post(in/de)crementation
						if(taille == 5) {
							if(mots[1].charAt(3) == '+'){//cas [,X+]
								reg2= mots[1].charAt(2);
								tmps= getR(reg2);
								tmpi= hextoInt(tmps);
								tmpi++;
							}
							else if(mots[1].charAt(3) == '-'){//cas [,X-]
								reg2= mots[1].charAt(2);
								tmps= getR(reg2);
								tmpi= hextoInt(tmps);
								tmpi--;
							}
							tmps = intToHex(tmpi);
							setR(reg2, tmps);
							adresse = getR(reg2);//on recupere l'adresse
						}
						if(adresse.length()<3) 
							adresse = "00" + adresse;
						System.out.print("old adresse: "+adresse);
						adresse= (String)RAM.getData(adresse);			//on recupere une nouvelle adresse depuis l'adresse originale
						RAM.setData(adresse, valreg);
					}
					else
						System.out.println("hello");
						if(mots[1].charAt(1) == '$'&& mots[1].indexOf(',') > 0) {		//cas [$20,X]
							System.out.println("hello2");
							reg2= mots[1].charAt(taille-2);
							int pos_vir= mots[1].indexOf(',');
							deplacement = mots[1].substring(2, pos_vir);
							System.out.println("le deplacement est de: "+hextoInt(deplacement)+" le registre est: "+reg2);
							adresse = getR(reg2);
							int tmpi= hextoInt(adresse);
							tmpi   += hextoInt(deplacement);
							adresse = intToHex(tmpi);
							System.out.println(adresse);
							adresse = (String)RAM.getData(adresse);	//on recupere une nouvelle adresse depuis l'adresse originale
							RAM.setData(adresse, valreg);
					}
				}
			}
			else {
				int tmp_dep;
				int tmp_adr;
				if(mots[1].charAt(1) == ',' && taille < 3) {//cas A,X
					reg2= mots[1].charAt(2);//dans notre exp c'est X
					reg3= mots[1].charAt(0);//c'est A
					deplacement = getR(reg3);
					adresse = getR(reg2);
				}
				else if(mots[1].charAt(0) == '$'&& mots[1].indexOf(',') > 0){									//cas $20,X
					deplacement= mots[1].substring(1, mots[1].indexOf(','));					//verifier les substring!!
					reg2= mots[1].charAt(taille-1);
					adresse = getR(reg2);
				}
				if(deplacement != null || adresse != null) {
					tmp_dep= hextoInt(deplacement);
					tmp_adr= hextoInt(adresse);
					tmp_adr =+ tmp_dep;
					adresse= intToHex(tmp_adr);
					RAM.setData(adresse, valreg);
				}
			}
		}
	}
	
	public int instr_ld(char registre, String[] mots) {
		if(mots[1].charAt(0) == '#') {		//Mode d'adressage immediat
    		String val = mots[1].substring(2);
    		String val4 = mots[1].substring(2);
    		if (mots[1].length() == 6) {
    			val4 = mots[1].substring(2, 6);
    		}
        	switch(registre) {
        		case 'A':
        			ArchitecteInterne.setA(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.setStringToColumn1(idrom, "86");
                    idrom++;
                    ROM.setStringToColumn1(idrom, val);
                    idrom++;
        			break;
        		case 'B':
        			ArchitecteInterne.setB(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.setStringToColumn1(idrom, "C6");
                    idrom++;
                    ROM.setStringToColumn1(idrom, val);
                    idrom++;
        			break;
        		case 'D':
        			ArchitecteInterne.setD(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "CC");
                    idrom++;
                    ROM.setStringToColumn1(idrom, val);
                    idrom++;
                    idrom++;
        			break;
        		case 'X':
        			ArchitecteInterne.setX(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "8E");
                    idrom++;
                    ROM.setStringToColumn1(idrom, val);
                    idrom++;
                    idrom++;
        			break;
        		case 'Y':
        			ArchitecteInterne.setY(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "10");
                    idrom++;
                    ROM.setStringToColumn1(idrom, "8E");
                    idrom++;
                    ROM.setStringToColumn1(idrom, val);
                    idrom++;
                    idrom++;
        			break;
        		case 'S':
        			ArchitecteInterne.setS(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "10");
                    idrom++;
                    ROM.setStringToColumn1(idrom, "CE");
                    idrom++;
                    ROM.setStringToColumn1(idrom, val);
                    idrom++;
                    idrom++;
        			break;
        		case 'U':
        			ArchitecteInterne.setU(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "CE");
                    idrom++;
                    ROM.setStringToColumn1(idrom, val);
                    idrom++;
                    idrom++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
    	}
		if (mots[1].charAt(0) == '$') {
		int size = mots[1].length();
		if (size == 5)		//Mode d'adressage etendu
		{
			String adresse = mots[1].substring(1, 5);
			String val = (String) RAM.getData(adresse);
			String val_next = (String) RAM.getDatapre(adresse);
			String val4 = (String) val + val_next;
        	switch(registre) {
        		case 'A':
        			ArchitecteInterne.setA(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.setStringToColumn1(idrom, "B6");
                    idrom++;
                    ROM.setStringToColumn1(idrom, adresse);
                    idrom++;
                    idrom++;
        			break;
        		case 'B':
        			ArchitecteInterne.setB(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.setStringToColumn1(idrom, "F6");
                    idrom++;
                    ROM.setStringToColumn1(idrom, adresse);
                    idrom++;
                    idrom++;
        			break;
        		case 'D':
        			ArchitecteInterne.setD(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "FC");
                    idrom++;
                    ROM.setStringToColumn1(idrom, adresse);
                    idrom++;
                    idrom++;
        			break;
        		case 'X':
        			ArchitecteInterne.setX(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "BE");
                    idrom++;
                    ROM.setStringToColumn1(idrom, adresse);
                    idrom++;
                    idrom++;
        			break;
        		case 'Y':
        			ArchitecteInterne.setY(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "10");
                    idrom++;
                    ROM.setStringToColumn1(idrom, "BE");
                    idrom++;
                    ROM.setStringToColumn1(idrom, adresse);
                    idrom++;
                    idrom++;
        			break;
        		case 'S':
        			ArchitecteInterne.setS(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "10");
                    idrom++;
                    ROM.setStringToColumn1(idrom, "FE");
                    idrom++;
                    ROM.setStringToColumn1(idrom, adresse);
                    idrom++;
                    idrom++;
        			break;
        		case 'U':
        			ArchitecteInterne.setU(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "FE");
                    idrom++;
                    ROM.setStringToColumn1(idrom, adresse);
                    idrom++;
                    idrom++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
			
		} else if (size == 3) {		//Mode d'adressage direct
			String adresse = (String) ArchitecteInterne.getDP() + mots[1].substring(1, 3);
			String add1 = adresse.substring(2);
			String val = (String) RAM.getData(adresse);
			String val_next = (String) RAM.getDatapre(adresse);
			String val4 = (String) val + val_next;
        	switch(registre) {
        		case 'A':
        			ArchitecteInterne.setA(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.setStringToColumn1(idrom, "96");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add1);
                    idrom++;
        			break;
        		case 'B':
        			ArchitecteInterne.setB(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.setStringToColumn1(idrom, "D6");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add1);
                    idrom++;
        			break;
        		case 'D':
        			ArchitecteInterne.setD(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "DC");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add1);
                    idrom++;
        			break;
        		case 'X':
        			ArchitecteInterne.setX(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "9E");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add1);
                    idrom++;
        			break;
        		case 'Y':
        			ArchitecteInterne.setY(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "10");
                    idrom++;
                    ROM.setStringToColumn1(idrom, "9E");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add1);
                    idrom++;
        			break;
        		case 'S':
        			ArchitecteInterne.setS(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "10");
                    idrom++;
                    ROM.setStringToColumn1(idrom, "DE");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add1);
                    idrom++;
        			break;
        		case 'U':
        			ArchitecteInterne.setU(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.setStringToColumn1(idrom, "DE");
                    idrom++;
                    ROM.setStringToColumn1(idrom, add1);
                    idrom++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
		}
		}
		return 1;
	}

	private static String intToHex(int value) {
        // Convert to hexadecimal
        String hexValue = Integer.toHexString(value).toUpperCase();

        // Pad with leading zeros to ensure a fixed width of 4 characters
        while (hexValue.length() < 4) {
            hexValue = "0" + hexValue;
        }

        return hexValue;
    }

	private static String hexToBinary(String hex, int nbrBits) {
        String binary = Integer.toBinaryString(Integer.parseInt(hex, 16));
        // Ajouter des zéros à gauche pour atteindre le nombre de bits spécifié
        while (binary.length() < nbrBits) {
            binary = "0" + binary;
        }
        return binary;
    }

	private static int hextoInt(String hexValue) {
		while (hexValue.length() < 5) {
	        hexValue = "0" + hexValue;
	    }

	    // Convert hexadecimal to integer
	    int intValue = Integer.parseInt(hexValue, 16);

	    return intValue;
	}

	private static String getR(char R) {
		switch(R) {
		case 'A':
			return ArchitecteInterne.getA();
		case 'B':
			return ArchitecteInterne.getB();
		case 'D':
			return ArchitecteInterne.getD();
		case 'X':
			return ArchitecteInterne.get_X();
		case 'Y':
			return ArchitecteInterne.get_Y();
		case 'S':
			return ArchitecteInterne.getS();
		case 'U':
			return ArchitecteInterne.getU();
		default:
            throw new IllegalArgumentException("Registre non pris en charge : " + R);
		}
	}
	
	private static void setR(char R, String val) {
		int z = val.length();
		String val2 = "00";
		String val4 = "0000";
		if (z == 4) {
			val4 = val;
			val2 = val.substring(2);
		}
		else if (z == 2) {
			val2 = val;
			val4 = (String) ArchitecteInterne.getDP() + val;
		}
		
		switch(R) {
		case 'A':
			ArchitecteInterne.setA(val2);
			flagNeg(val2,8);
			flagZero(val2);
			break;
		case 'B':
			ArchitecteInterne.setB(val2);
			flagNeg(val2,8);
			flagZero(val2);
			break;
		case 'D':
			ArchitecteInterne.setD(val4);
			flagNeg(val4,16);
			flagZero(val4);
			break;
		case 'X':
			ArchitecteInterne.setX(val4);
			flagNeg(val4,16);
			flagZero(val4);
			break;
		case 'Y':
			ArchitecteInterne.setY(val4);
			flagNeg(val4,16);
			flagZero(val4);
			break;
		case 'S':
			ArchitecteInterne.setS(val4);
			flagNeg(val4,16);
			flagZero(val4);
			break;
		case 'U':
			ArchitecteInterne.setU(val4);
			flagNeg(val4,16);
			flagZero(val4);
			break;
		default:
            throw new IllegalArgumentException("Registre non pris en charge : " + R);
		}
	}
	
	private static String addBinary(String binary1, String binary2, int nbrBits) {
        int num1 = Integer.parseInt(binary1, 2);
        int num2 = Integer.parseInt(binary2, 2);
        int sum = num1 + num2;
        String binarySum = Integer.toBinaryString(sum);
        while (binarySum.length() < nbrBits) {
            binarySum = "0" + binarySum;
        }
        return binarySum;
    }
	
	private static String subtractBinary(String binary1, String binary2) {
	    // Convertir les nombres binaires en décimaux
	    int num1 = Integer.parseInt(binary1, 2);
	    int num2 = Integer.parseInt(binary2, 2);

	    // Soustraire les décimaux
	    int difference = num1 - num2;

	    // Convertir le résultat en binaire
	    return Integer.toBinaryString(difference);
	}
	
	private static boolean debordementDetectADD(String op1, String op2, int nbrBits) {
        // Convertir les opérandes hexadécimales en binaire
        String binaryOp1 = hexToBinary(op1,nbrBits);
        String binaryOp2 = hexToBinary(op2,nbrBits);

        // Effectuer l'addition binaire
        String binaryResult = addBinary(binaryOp1, binaryOp2, nbrBits);

        // Vérifier le débordement par comparaison des signes
        char signOp1 = binaryOp1.charAt(0);
        char signOp2 = binaryOp2.charAt(0);
        char signResult = binaryResult.charAt(0);

        if ((signOp1 == signOp2) && (signOp1 != signResult)) {
            return true; // Débordement détecté
        }

        // Vérifier le dépassement de la capacité
        if (binaryResult.length() > nbrBits) {
            return true; // Débordement détecté
        }

        return false; // Pas de débordement
    }
	
	private static boolean debordementDetectSUB(String op1, String op2, int nbrBits) {
	    // Convertir les opérandes hexadécimales en binaire
	    String binaryOp1 = hexToBinary(op1, nbrBits);
	    String binaryOp2 = hexToBinary(op2, nbrBits);

	    // Effectuer la soustraction binaire
	    String binaryResult = subtractBinary(binaryOp1, binaryOp2);

	    // Vérifier le débordement par comparaison des signes
	    char signOp1 = binaryOp1.charAt(0);
	    char signOp2 = binaryOp2.charAt(0);
	    char signResult = binaryResult.charAt(0);

	    if ((signOp1 != signOp2) && (signOp1 != signResult)) {
	        return true; // Débordement détecté
	    }

	    // Vérifier le dépassement de la capacité
	    if (binaryResult.length() > nbrBits) {
	        return true; // Débordement détecté
	    }

	    return false; // Pas de débordement
	}
	
	private static boolean debordementDetectMUL(String op1, String op2, int nbrBits) {
        String binaryOp1 = hexToBinary(op1, nbrBits);
        String binaryOp2 = hexToBinary(op2, nbrBits);

        long num1 = Long.parseLong(binaryOp1, 2);
        long num2 = Long.parseLong(binaryOp2, 2);

        if (num1 * num2 >= Math.pow(2, nbrBits)) {
            return true; // Débordement détecté
        }

        return false; // Pas de débordement
    }

    private static boolean detectCarryMultiplication(String op1, String op2, int nbrBits) {
        String binaryOp1 = hexToBinary(op1, nbrBits);
        String binaryOp2 = hexToBinary(op2, nbrBits);

        long num1 = Long.parseLong(binaryOp1, 2);
        long num2 = Long.parseLong(binaryOp2, 2);

        String product = Long.toBinaryString(num1 * num2);
        return product.length() > nbrBits; // Retenue détectée si la longueur du produit dépasse nbrBits
    }
	
    public static boolean testBitPoidsFort(String hex, int nbrBits) {
        // Convertir les valeurs hexadécimales en binaire
        String binary1 = hexToBinary(hex,nbrBits);
        // Ajouter les 0
        char msb = binary1.charAt(0);
        // Vérifier si le bit de poids fort est positif (0) ou négatif (1)
        return msb == '1';
    }
    
    private static boolean detectCarryAddition(String op1, String op2, int nbrBits) {
        // Convertir les opérandes hexadécimales en binaire
        String binaryOp1 = hexToBinary(op1, nbrBits);
        String binaryOp2 = hexToBinary(op2, nbrBits);

        // Effectuer l'addition binaire
        String binaryResult = addBinary(binaryOp1, binaryOp2,nbrBits);

        // Vérifier la retenue en comparant la longueur des résultats
        return binaryResult.length() > nbrBits;
    }
    
    private static boolean detectCarrySubtraction(String op1, String op2, int nbrBits) {
        // Convertir les opérandes hexadécimales en binaire
        String binaryOp1 = hexToBinary(op1, nbrBits);
        String binaryOp2 = hexToBinary(op2, nbrBits);

        // Effectuer la soustraction binaire
        String binaryResult = subtractBinary(binaryOp1, binaryOp2);

        // Vérifier la retenue en comparant la longueur des résultats
        return binaryResult.length() > nbrBits;
    }

    public static void flagADD(String op1,String op2, int nbrBits) {
    	if(debordementDetectADD(op1,op2,nbrBits))
    		ArchitecteInterne.setV("1");
    	else
    		ArchitecteInterne.setV("0");
    	if(detectCarryAddition(op1,op2,nbrBits))
    		ArchitecteInterne.setC("1");
    	else
    		ArchitecteInterne.setC("0");
    		
    }
    
    public static void flagSUB(String op1,String op2,int nbrBits) {
    	if(debordementDetectSUB(op1,op2,nbrBits))
    		ArchitecteInterne.setV("1");
    	else
        		ArchitecteInterne.setV("0");
    	if(detectCarrySubtraction(op1,op2,nbrBits))
    		ArchitecteInterne.setC("1");
    	else
    		ArchitecteInterne.setC("0");
    }
    
    public static void flagMUL(String op1,String op2,int nbrBits) {
    	if(debordementDetectMUL(op1,op2,nbrBits))
    		ArchitecteInterne.setV("1");
    	else
        		ArchitecteInterne.setV("0");
    	if(detectCarryMultiplication(op1,op2,nbrBits))
    		ArchitecteInterne.setC("1");
    	else
    		ArchitecteInterne.setC("0");
    }
 
    public static void flagNeg(String op, int nbrBits) {
    	if(testBitPoidsFort(op,nbrBits)) {
    		ArchitecteInterne.setN("1");
    	}
    	else 
    		ArchitecteInterne.setN("0");
    }
    
    private static void flagZero(String hexValue) {
        // Convert hexadecimal to integer
        int value = Integer.parseInt(hexValue, 16);
        // Update the Z flag in ArchitecteInterne based on the value
        if (value == 0) {
            ArchitecteInterne.setZ("1");
        } else {
            ArchitecteInterne.setZ("0");
        }
    }
    
	private void find_all_etq(String asmbl) {
		int k = 0;
		String[] ligne = asmbl.split("\\n");
		while (!ligne[k].equals("END")) {
			if (ligne[k].charAt(ligne[k].length()-1) == ':') {
    			pos = k;
    			System.out.println("eitiquette ligne: "+k);
    			etiquette.add(ligne[k]+pos);
    		}
			k++;
		}
	}

	private void goto_etiquette (String mots) {
		for(String this_etiquette : etiquette) {
			if(this_etiquette.substring(0, this_etiquette.length()-2).equals(mots)) {
				i = this_etiquette.charAt(this_etiquette.length()-1) - '0';
				break;
			}
		}
	}

}