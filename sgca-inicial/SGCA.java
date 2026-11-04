import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SGCA {
    static List<Alumno> a = new ArrayList<>();
    static List<Solicitud> s = new ArrayList<>();
    static List<Certificado> c = new ArrayList<>();
    static int n = 1;

    public static void main(String[] args) {
        a.add(new Alumno(1, "Ana Ejemplo", false, true,
                new String[]{"Programacion", "Bases de datos"},
                new int[]{8, 6}));
        a.add(new Alumno(2, "Luis Ejemplo", true, true,
                new String[]{"Programacion"}, new int[]{7}));
        a.add(new Alumno(3, "Eva Ejemplo", false, false,
                new String[]{"Programacion"}, new int[]{9}));
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("\nSGCA - prototipo inicial");
                System.out.println("1 Registrar solicitud\n2 Emitir certificado\n3 Consultar solicitud\n4 Listar solicitudes\n0 Salir");
                String op = sc.nextLine();
                try {
                    if (op.equals("0")) break;
                    if (op.equals("1")) {
                        System.out.println("Alumno: 1 Ana, 2 Luis (con deuda), 3 Eva (no regular)");
                        int id = Integer.parseInt(sc.nextLine());
                        System.out.println("Tipo: 1 Regular, 2 Analitico parcial");
                        int t = Integer.parseInt(sc.nextLine());
                        hacer(1, id, t, 0, "", false);
                    } else if (op.equals("2")) {
                        System.out.println("Numero de solicitud:");
                        hacer(2, 0, 0, Integer.parseInt(sc.nextLine()), "", false);
                    } else if (op.equals("3")) {
                        System.out.println("Numero de solicitud:");
                        hacer(3, 0, 0, Integer.parseInt(sc.nextLine()), "", false);
                    } else if (op.equals("4")) {
                        hacer(4, 0, 0, 0, "", false);
                    } else {
                        System.out.println("Opcion invalida");
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Debe ingresar un numero entero");
                }
            }
        }
    }

    static void registrarSolicitud(int id, int t) {
    Alumno x = null;
    for (Alumno z : a) if (z.id == id) x = z;
    if (x == null) {
        System.out.println("Alumno inexistente");
        return;
    }
    if (t != 1 && t != 2) {
        System.out.println("Tipo invalido");
        return;
    }
    Solicitud y = new Solicitud();
    y.id = n++;
    y.alumno = id;
    y.tipo = t;
    y.fecha = LocalDate.now().toString();
    y.estado = "REGISTRADA";
    s.add(y);
    System.out.println("Solicitud registrada: " + y.id);
}

    static void hacer(int op, int id, int t, int numero, String nombre, boolean deuda) {
        if (op == 1) {
            registrarSolicitud(id, t);
        }else if (op == 2) {
            Solicitud y = null;
            for (Solicitud z : s) if (z.id == numero) y = z;
            if (y == null) { System.out.println("Solicitud inexistente"); return; }
            if (y.estado.equals("EMITIDA")) { System.out.println("Solicitud ya emitida"); return; }
            Alumno x = null;
            for (Alumno z : a) if (z.id == y.alumno) x = z;
            if (x == null) { System.out.println("Alumno inexistente"); return; }
            nombre = x.nombre;
            deuda = x.deuda;
            if (deuda) { System.out.println("No se emite: alumno con deuda"); return; }
            String texto = "";
            if (y.tipo == 1) {
                if (!x.regular) { System.out.println("No se emite: alumno no regular"); return; }
                texto = "CERTIFICADO DE ALUMNO REGULAR\nAlumno: " + nombre + "\nFecha: " + LocalDate.now();
                Certificado k = new Certificado();
                k.solicitud = y.id;
                k.fecha = LocalDate.now().toString();
                k.texto = texto;
                c.add(k);
                y.estado = "EMITIDA";
                System.out.println(texto);

            } else if (y.tipo == 2) {
                int total = 0;
                int cantidad = 0;
                texto = "ANALITICO PARCIAL\nAlumno: " + nombre + "\nFecha: " + LocalDate.now();
                for (int i = 0; i < x.notas.length; i++) {
                        if (x.notas[i] < 1 || x.notas[i] > 10) {
                            System.out.println("No se emite: nota fuera de escala"); return;
                        }
                        texto += "\n" + x.materias[i] + ": " + x.notas[i];
                        total += x.notas[i];
                        cantidad++;
                }
                texto += cantidad == 0 ? "\nPromedio: sin notas" : "\nPromedio: " + ((double) total / cantidad);
                Certificado k = new Certificado();
                k.solicitud = y.id;
                k.fecha = LocalDate.now().toString();
                k.texto = texto;
                c.add(k);
                y.estado = "EMITIDA";
                System.out.println(texto);

            }
        } else if (op == 3) {
            Solicitud y = null;
            for (Solicitud z : s) if (z.id == numero) y = z;
            if (y == null) { System.out.println("Solicitud inexistente"); return; }
            System.out.println(y.id + " | alumno=" + y.alumno + " | tipo=" + y.tipo + " | fecha=" + y.fecha + " | " + y.estado);
            for (Certificado k : c) if (k.solicitud == y.id) System.out.println(k.texto);
        } else if (op == 4) {
            if (s.isEmpty()) System.out.println("Sin solicitudes");
            for (Solicitud y : s) {
                System.out.println(y.id + " | alumno=" + y.alumno + " | tipo=" + y.tipo + " | fecha=" + y.fecha + " | " + y.estado);
            }
        }
    }
}

class Alumno {
    int id;
    String nombre;
    boolean deuda;
    boolean regular;
    String[] materias;
    int[] notas;
    Alumno(int id, String nombre, boolean deuda, boolean regular,
           String[] materias, int[] notas) {
        this.id = id; this.nombre = nombre; this.deuda = deuda;
        this.regular = regular; this.materias = materias; this.notas = notas;
    }
}
class Solicitud {
    int id;
    int alumno;
    int tipo;
    String fecha;
    String estado;
}
class Certificado {
    int solicitud;
    String fecha;
    String texto;
}
