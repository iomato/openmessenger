package openmessenger

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder

class UserEvent implements Serializable {
  
  User user
  Event event

  boolean equals(other) {
    if (!(other instanceof UserEvent)) {
      return false
    }

    other.user?.id == user?.id &&
      other.event?.id == event?.id
  }
  
  int hashCode() {
    def builder = new HashCodeBuilder()
    if (user) builder.append(user.id)
    if (event) builder.append(event.id)
    builder.toHashCode()
  }
  
  static UserEvent get(Long userId, Long eventId) {
    findByUserAndEvent(User.get(userId), Event.get(eventId))
  }

  static UserEvent create(User user, Event event, boolean flush = false) {
    new UserEvent(user: user, event: event).save(flush: flush, insert: true)
  }
  
    static void removeAll(User user) {
    executeUpdate 'DELETE FROM UserEvent WHERE user=:user', [user: user]
  }

  static void removeAll(Event event) {
    executeUpdate 'DELETE FROM UserEvent WHERE event=:event', [event: event]
  }
  
  static void remove(User user, Event event) {
    executeUpdate 'DELETE FROM UserEvent WHERE user=:user and event=:event', [user: user, event: event]
  }
  
  static mapping = {
    id composite: ['event', 'user']
    version false
  }
  
}
