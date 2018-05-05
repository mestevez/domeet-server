import { Model } from 'vue-mc'

/**
 * Subject model
 */
export default class Subject extends Model {
  options () {
    return {
      identifier: 'subject_id'
    }
  }

  // Attribute mutations.
  mutations () {
    return {
      subject_duration: Number
    }
  }

  routes () {
    return {
      save: '/app/meet/subject/{subject_id}',
      delete: '/app/meet/subject/{subject_id}'
    }
  }
}

export let SubjectPriority = {
  IRRELEVANT: 0,
  NORMAL: 1,
  ESSENTIAL: 2
}
