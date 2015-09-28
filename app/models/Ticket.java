package models;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.joda.time.DateTime;

import play.db.ebean.Model;

import com.avaje.ebean.Query;

@Entity
public class Ticket extends Model {
    public Date creationDate;
    @Id
    public long id;
    public Date lastUpdated;
    public boolean open = true;
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    @OrderBy("creationDate ASC")
    public List<TicketResponse> responses;
    @ManyToOne
    public Server server;
    @ManyToOne
    public User submitter;
    public String title;

    public Ticket(Server server, Date submitted, User submitter, String title) {
        this.server = server;
        this.lastUpdated = submitted;
        this.creationDate = submitted;
        this.submitter = submitter;
        this.title = title;
    }

    public Ticket(Server server, User submitter, String title) {
        this(server, DateTime.now().toDate(), submitter, title);
    }

    public void addResponse(User user, String content) {
        TicketResponse response = new TicketResponse(this, user, content);
        response.save();
    }

    public void close() {
        open = false;
        save();
    }

    public Server getServer() {
    	return server;
    }

    public TicketResponse getFirstResponse() {
        return responses.get(0);
    }

    public Collection<TicketResponse> getOrderedResponses() {
        return responses;
    }

    public boolean isAcceptingResponses() {
        return open;
    }

    public boolean isClosableBy(User user) {
        return user.equals(submitter) || user.equals(getServer().owner);
    }

    public boolean isVisibleFrom(User sessionUser) {
        return sessionUser != null && (sessionUser.equals(submitter) || sessionUser.equals(getServer().owner));
    }

    public static Ticket byId(Long id) {
        return finder.byId(id);
    }

    public static Query<Ticket> getByParticipantQuery(User user) {
        return finder.where().in("id", responseFinder.where().eq("responder", user).select("ticket")).query()
                .setDistinct(true);
    }

    public static Collection<Ticket> getByServer(Server server) {
        return getByServerQuery(server).findList();
    }

    public static Query<Ticket> getByServerQuery(Server server) {
        return finder.where().eq("server", server).orderBy("lastUpdated");
    }

    private static Model.Finder<Long, Ticket> finder = new Model.Finder<Long, Ticket>(Long.class, Ticket.class);
    private static Model.Finder<Long, TicketResponse> responseFinder = new Model.Finder<Long, TicketResponse>(
            Long.class, TicketResponse.class);
}