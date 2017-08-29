package com.xebia.shopmanager.domain;

public class Session {
    public Session(long eta, Clerk clerk) {
        this.eta = eta;
        this.clerk = clerk;
    }

    private Clerk clerk;
    private long eta;

    public Clerk getClerk() {return clerk;}

    public void setClerk(Clerk clerk) {
        this.clerk = clerk;
    }

    public long getEta() {
        return this.eta;
    }

    public void setEta(long eta) {
        this.eta = eta;
    }

    @Override
    public String toString() {
        return "Session{" +
                "eta='" + eta + "'" +
                ", clerk=" + clerk +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Session session = (Session) o;
        return clerk.getUuid().equals(session.getClerk().getUuid());
    }

    @Override
    public int hashCode() {
        return clerk.hashCode();
    }

}
