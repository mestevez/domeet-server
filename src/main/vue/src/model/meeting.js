import { Model } from 'vue-mc'

/**
 * Task model
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
      meet_duration: Number
    }
  }

  routes () {
    return {
      save: '/app/meet/{meet_id}',
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
