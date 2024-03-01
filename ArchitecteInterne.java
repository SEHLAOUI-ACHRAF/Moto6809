package moto_6809;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.Font;

public class ArchitecteInterne extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private static JLabel instru_PC;
	private static JLabel PC;
	private static JLabel S;
	private static JLabel U;
	private static JLabel A;
	private static JLabel B;
	private static JLabel DP;
	private static JLabel X;
	private static JLabel Y;
	private static JLabel E;
	private static JLabel F;
	private static JLabel H;
	private static JLabel I;
	private static JLabel N;
	private static JLabel Z;
	private static JLabel V;
	private static JLabel C;

	public ArchitecteInterne() {
		setResizable(false);
		setAlwaysOnTop(true);
		setTitle("ARCHITECTURE INTERNE DU 6809");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(10, 120, 226, 420);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		PC = new JLabel("0000");
		PC.setFont(new Font("Tahoma", Font.PLAIN, 23));
		PC.setForeground(Color.BLUE);
		PC.setBounds(95, 15, 89, 26);
		contentPane.add(PC);
		
		instru_PC = new JLabel("");
		instru_PC.setFont(new Font("Tahoma", Font.PLAIN, 23));
		instru_PC.setForeground(Color.BLUE);
		instru_PC.setBounds(13, 50, 202, 31);
		contentPane.add(instru_PC);
		
		S = new JLabel("0000");
		S.setFont(new Font("Tahoma", Font.PLAIN, 23));
		S.setForeground(Color.BLUE);
		S.setBounds(40, 85, 70, 30);
		contentPane.add(S);
		
		U = new JLabel("0000");
		U.setFont(new Font("Tahoma", Font.PLAIN, 23));
		U.setForeground(Color.BLUE);
		U.setBounds(145, 85, 70, 30);
		contentPane.add(U);
		
		A = new JLabel("00");
		A.setFont(new Font("Tahoma", Font.PLAIN, 23));
		A.setForeground(Color.BLUE);
		A.setBounds(40, 141, 46, 30);
		contentPane.add(A);
		
		B = new JLabel("00");
		B.setFont(new Font("Tahoma", Font.PLAIN, 23));
		B.setForeground(Color.BLUE);
		B.setBounds(40, 225, 46, 30);
		contentPane.add(B);
		
		DP = new JLabel("00");
		DP.setFont(new Font("Tahoma", Font.PLAIN, 23));
		DP.setForeground(Color.BLUE);
		DP.setBounds(45, 281, 46, 30);
		contentPane.add(DP);
		
		X = new JLabel("0000");
		X.setFont(new Font("Tahoma", Font.PLAIN, 23));
		X.setForeground(Color.BLUE);
		X.setBounds(30, 352, 76, 30);
		contentPane.add(X);
		
		Y = new JLabel("0000");
		Y.setFont(new Font("Tahoma", Font.PLAIN, 23));
		Y.setForeground(Color.BLUE);
		Y.setBounds(145, 353, 76, 30);
		contentPane.add(Y);
		
		E = new JLabel("0");
		E.setForeground(new Color(0, 0, 160));
		E.setFont(new Font("Tahoma", Font.PLAIN, 23));
		E.setBounds(92, 281, 13, 31);
		contentPane.add(E);
		
		F = new JLabel("0");
		F.setForeground(new Color(0, 0, 160));
		F.setFont(new Font("Tahoma", Font.PLAIN, 23));
		F.setBounds(105, 281, 13, 31);
		contentPane.add(F);
		
		H = new JLabel("0");
		H.setForeground(new Color(0, 0, 160));
		H.setFont(new Font("Tahoma", Font.PLAIN, 23));
		H.setBounds(117, 281, 13, 31);
		contentPane.add(H);
		
		I = new JLabel("0");
		I.setForeground(new Color(0, 0, 160));
		I.setFont(new Font("Tahoma", Font.PLAIN, 23));
		I.setBounds(130, 281, 13, 31);
		contentPane.add(I);
		
		N = new JLabel("0");
		N.setForeground(new Color(0, 0, 160));
		N.setFont(new Font("Tahoma", Font.PLAIN, 23));
		N.setBounds(143, 281, 13, 31);
		contentPane.add(N);
		
		Z = new JLabel("1");
		Z.setForeground(new Color(0, 0, 160));
		Z.setFont(new Font("Tahoma", Font.PLAIN, 23));
		Z.setBounds(156, 281, 13, 31);
		contentPane.add(Z);
		
		V = new JLabel("0");
		V.setForeground(new Color(0, 0, 160));
		V.setFont(new Font("Tahoma", Font.PLAIN, 23));
		V.setBounds(169, 281, 13, 31);
		contentPane.add(V);
		
		C = new JLabel("0");
		C.setForeground(new Color(0, 0, 160));
		C.setFont(new Font("Tahoma", Font.PLAIN, 23));
		C.setBounds(182, 281, 13, 31);
		contentPane.add(C);
		
		JLabel lblNewLabel = new JLabel();
		lblNewLabel.setIcon(new ImageIcon("/UPanle.png"));
		lblNewLabel.setBounds(2, 2, 210, 380);
		contentPane.add(lblNewLabel);
	}

	public static void setA(String a) {
		A.setText(a);
	}
	public static String getA() {
		String a = A.getText();
		return a;
	}
	public static void setB(String a) {
		B.setText(a);
	}
	public static String getB() {
		String a = B.getText();
		return a;
	}
	public static void setD(String a) {
		A.setText(a.substring(0, 2));
		System.out.println(a.substring(0, 2));
		B.setText(a.substring(2));
		System.out.println(a.substring(2));
	}
	public static String getD() {
		String a = (String) A.getText() + B.getText();
		return a;
	}
	public static void setX(String a) {
		X.setText(a);
	}
	public static String get_X() {
		String a = X.getText();
		return a;
	}
	public static void setY(String a) {
		Y.setText(a);
	}
	public static String get_Y() {
		String a = Y.getText();
		return a;
	}
	public static void setU(String a) {
		U.setText(a);
	}
	public static String getU() {
		String a = U.getText();
		return a;
	}
	public static void setS(String a) {
		S.setText(a);
	}
	public static String getS() {
		String a = S.getText();
		return a;
	}
	public static void setDP(String a) {
		DP.setText(a);
	}
	public static String getDP() {
		String a = DP.getText();
		return a;
	}
	public static void setPC(String a) {
		PC.setText(a);
	}
	public static void setinstru_PC(String a) {
		instru_PC.setText(a);
	}
	public static void setC(String c) {
        C.setText(c);
    }

    public static String getC() {
        String c = C.getText();
        return c;
    }

    public static void setV(String v) {
        V.setText(v);
    }

    public static String getV() {
        String v = V.getText();
        return v;
    }

    public static void setZ(String z) {
        Z.setText(z);
    }

    public static String getZ() {
        String z = Z.getText();
        return z;
    }

    public static void setN(String n) {
        N.setText(n);
    }

    public static String getN() {
        String n = N.getText();
        return n;
    }

    public static void setI(String i) {
        I.setText(i);
    }

    public static String getI() {
        String i = I.getText();
        return i;
    }

    public static void setH(String h) {
        H.setText(h);
    }

    public static String getH() {
        String h = H.getText();
        return h;
    }

    public static void setF(String f) {
        F.setText(f);
    }

    public static String getF() {
        String f = F.getText();
        return f;
    }

    public static void setE(String e) {
        E.setText(e);
    }

    public static String getE() {
        String e = E.getText();
        return e;
    }

    public static void resetValues() {
        // Set initial values for all JLabels
        PC.setText("0000");
        instru_PC.setText("");
        S.setText("0000");
        U.setText("0000");
        A.setText("00");
        B.setText("00");
        DP.setText("00");
        X.setText("0000");
        Y.setText("0000");
        E.setText("0");
        F.setText("0");
        H.setText("0");
        I.setText("0");
        N.setText("0");
        Z.setText("1");
        V.setText("0");
        C.setText("0");
    }
	
}