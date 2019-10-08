package com.example.showtime;

public class TicketBookingProperties {

        String name;
        String email;
        String movie;
        String cinema;
        int tickets;
        String date;

        public TicketBookingProperties(String name, String email, String movie, String cinema, int tickets, String date) {
            this.name = name;
            this.email = email;
            this.movie = movie;
            this.cinema = cinema;
            this.tickets = tickets;
            this.date = date;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMovie() {
            return movie;
        }

        public void setMovie(String movie) {
            this.movie = movie;
        }

        public String getCinema() {
            return cinema;
        }

        public void setCinema(String cinema) {
            this.cinema = cinema;
        }

        public int getTickets() {
            return tickets;
        }

        public void setTickets(int tickets) {
            this.tickets = tickets;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
}
