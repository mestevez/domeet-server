import { Model, Collection } from 'vue-mc'
import Subject from '@/model/subject'
import Attendant from '@/model/attendant'

/**
 * Meeting model
 */
export default class Meeting extends Model {
  options () {
    return {
      identifier: 'meet_id'
    }
  }

  // Attribute mutations.
  mutations () {
    return {
      meet_duration: Number,
      subjects: (subjects) => {
        if (!(subjects instanceof SubjectsList)) {
          let subjectsList = new SubjectsList()
          subjectsList.add(subjects)
          subjects = subjectsList
        }

        return subjects
      },
      attendants: (attendants) => {
        let attendantsList

        if (attendants instanceof AttendantsList) {
          attendantsList = attendants
        } else {
          attendantsList = new AttendantsList()
          attendantsList.add(attendants)
        }

        return attendantsList
      }
    }
  }

  routes () {
    return {
      save: '/app/meet/{meet_id}',
      fetch: '/app/meet/{meet_id}',
      delete: '/app/meet/{meet_id}'
    }
  }
}

export let MeetingState = {
  EDIT: 0,
  READY: 1,
  CANCELLED: 2,
  STARTED: 3,
  ENDED: 4,
  CONCLUDED: 5,
  MAIL_SENT: 6
}

class SubjectsList extends Collection {
  model () {
    return Subject
  }

  routes () {
    return {
      fetch: '/app/meet/{meet_id}/subjects'
    }
  }
}

class AttendantsList extends Collection {
  model () {
    return Attendant
  }

  routes () {
    return {
      fetch: '/app/meet/{meet_id}/attendants'
    }
  }
}
