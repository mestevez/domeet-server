import { Model, Collection } from 'vue-mc'

/**
 * MeetingIssue model
 */
export class MeetingIssueModel extends Model {
}

export default class MeetingIssues extends Collection {
  defaults () {
    return {
      meet_id: -1
    }
  }

  options () {
    return {
      model: MeetingIssueModel
    }
  }

  routes () {
    return {
      fetch: '/app/meet/{meet_id}/issues'
    }
  }
}

export let MeetingIssueType = {
  INFO: 'INFO',
  WARNING: 'WARNING',
  ERROR: 'ERROR'
}
