package main.java;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.java.util.DateTimeUtil;

public class EventoSystemApp {
    private static final String EVENT_FILE = "events.data";
    private static final String USER_FILE = "users.data";
    private static List<Evento> eventos = new ArrayList<>();
    private static List<Usuario> usuarios = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        carregarUsuarios();
        salvarUsuarios();
        carregarEventos();
        salvarEventos();
        mostrarMenu();
    }

    private static void mostrarMenu() {
        int escolha = -1;

        while (escolha != 0) {
            System.out.println("=== Sistema de Eventos ===");
            System.out.println("1. Cadastrar Evento");
            System.out.println("2. Consultar Eventos");
            System.out.println("3. Participar de Evento");
            System.out.println("4. Meus Eventos");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            escolha = scanner.nextInt();

            scanner.nextLine();

            switch (escolha) {
                case 1:
                    cadastrarEvento();
                    break;
                case 2:
                    consultarEventos();
                    break;
                case 3:
                    participarEvento();
                    break;
                case 4:
                    meusEventos();
                    break;
                case 0:
                    System.out.println("Saindo do programa...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        }
    }

    private static void cadastrarEvento() {
        System.out.println("=== Cadastro de Evento ===");
        System.out.print("Nome: ");
        String nomeEvento = scanner.next();
        System.out.print("Endereço: ");
        String enderecoEvento = scanner.next();
        System.out.print("Categoria: ");
        String categoriaEvento = scanner.next();

        System.out.print("Data (dd/MM/yyyy): ");
        String dataStr = scanner.next();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataEvento;
        try {
            dataEvento = LocalDate.parse(dataStr, dateFormatter);
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data inválido. Use o formato dd/MM/yyyy.");
            return;
        }

        System.out.print("Horário (HH:mm): ");
        String horarioStr = scanner.next();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime horarioEvento;
        try {
            horarioEvento = LocalTime.parse(horarioStr, timeFormatter);
        } catch (DateTimeParseException e) {
            System.out.println("Formato de hora inválido. Use o formato HH:mm.");
            return;
        }

        System.out.print("Descrição: ");
        String descricaoEvento = scanner.next();

        Evento evento = new Evento(nomeEvento, enderecoEvento, categoriaEvento, dataEvento.atTime(horarioEvento),
                descricaoEvento);
        eventos.add(evento);
        System.out.println("Evento cadastrado com sucesso!");
    }

    private static void consultarEventos() {
        System.out.println("=== Consulta de Eventos ===");
        for (Evento evento : eventos) {
            System.out.println("Nome: " + evento.getNome());
            System.out.println("Endereço: " + evento.getEndereco());
            System.out.println("Categoria: " + evento.getCategoria());
            System.out.println("Horário: " + DateTimeUtil.formatDateTime(evento.getHorario()));
            System.out.println("Descrição: " + evento.getDescricao());
            System.out.println();
        }
    }

    private static void participarEvento() {
        System.out.println("=== Participar de Evento ===");
        System.out.print("Informe seu nome de usuário: ");
        String nomeUsuario = scanner.next();

        Usuario usuario = buscarUsuario(nomeUsuario);
        if (usuario == null) {
            System.out.println("Usuário não encontrado.");
            return;
        }

        System.out.print("Escolha o evento pelo nome: ");
        String nomeEvento = scanner.next();

        Evento evento = buscarEvento(nomeEvento);
        if (evento == null) {
            System.out.println("Evento não encontrado.");
            return;
        }

        if (eventoEstaParticipando(usuario, evento)) {
            System.out.println("Você já está participando deste evento.");
            return;
        }

        evento.adicionarParticipante(usuario);
        System.out.println("Participação confirmada!");
    }

    private static void meusEventos() {
        System.out.println("=== Meus Eventos ===");
        System.out.print("Informe seu nome de usuário: ");
        String nomeUsuario = scanner.next();

        Usuario usuario = buscarUsuario(nomeUsuario);
        if (usuario == null) {
            System.out.println("Usuário não encontrado.");
            return;
        }

        System.out.println("Eventos em que você está participando:");
        for (Evento evento : eventos) {
            if (eventoEstaParticipando(usuario, evento)) {
                System.out.println("Nome do evento: " + evento.getNome());
                System.out.println("Endereço: " + evento.getEndereco());
                System.out.println("Horário: " + DateTimeUtil.formatDateTime(evento.getHorario()));
                System.out.println("Descrição: " + evento.getDescricao());
                System.out.println();
            }
        }
    }

    private static Usuario buscarUsuario(String nomeUsuario) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNome().equals(nomeUsuario)) {
                return usuario;
            }
        }
        return null;
    }

    private static Evento buscarEvento(String nomeEvento) {
        for (Evento evento : eventos) {
            if (evento.getNome().equals(nomeEvento)) {
                return evento;
            }
        }
        return null;
    }

    private static boolean eventoEstaParticipando(Usuario usuario, Evento evento) {
        List<Usuario> participantes = evento.getParticipantes();
        for (Usuario participante : participantes) {
            if (participante.getNome().equals(usuario.getNome())) {
                return true;
            }
        }
        return false;
    }

    private static void carregarEventos() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(EVENT_FILE))) {
            Object object = in.readObject();
            if (object instanceof List<?>) {
                List<?> objList = (List<?>) object;
                for (Object obj : objList) {
                    if (obj instanceof Evento) {
                        eventos.add((Evento) obj);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            eventos = new ArrayList<>();
        }
    }

    private static void carregarUsuarios() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(USER_FILE))) {
            Object object = in.readObject();
            if (object instanceof List<?>) {
                List<?> objList = (List<?>) object;
                for (Object obj : objList) {
                    if (obj instanceof Usuario) {
                        usuarios.add((Usuario) obj);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            usuarios = new ArrayList<>();
        }
    }

    private static void salvarUsuarios() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            out.writeObject(usuarios);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void salvarEventos() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(EVENT_FILE))) {
            out.writeObject(eventos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
