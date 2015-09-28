package models;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.joda.time.DateTime;

import play.db.ebean.Model;
import util.Util;

@Entity
public class TicketResponse extends Model {
    public Date creationDate;
    @Id
    public long id;
    public Date lastEditedDate;
    @Lob
    public String rawText;
    @ManyToOne(fetch = FetchType.LAZY)
    public User responder;
    @ManyToOne(fetch = FetchType.LAZY)
    public Ticket ticket;

    public TicketResponse(Ticket ticket, User responder, String content) {
        this.ticket = ticket;
        this.responder = responder;
        this.rawText = content;
        this.creationDate = lastEditedDate = DateTime.now().toDate();
    }

    public String getMarkdownContent() {
        return Util.renderMarkdown(rawText);
    }

    public User getResponder() {
        return responder;
    }

    public boolean isEditableBy(User user) {
        return user.equals(responder) || ticket.server.owner.equals(user);
    }

    public void setBody(String string) {
        this.rawText = string;
        this.lastEditedDate = new Date();
    }

    public static TicketResponse byId(long id) {
        return find.byId(id);
    }

    public static Collection<TicketResponse> byUser(User user) {
        return find.where().eq("responder", user).findList();
    }

    private static Model.Finder<Long, TicketResponse> find = new Model.Finder<Long, TicketResponse>(Long.class,
            TicketResponse.class);
}