import { Component, Input, OnInit } from '@angular/core';
import { User } from '@app/classes/user.class';

@Component({
  selector: 'user-short-info',
  templateUrl: './user-short-info.component.html',
  styleUrls: ['./user-short-info.component.scss']
})
export class UserShortInfoComponent implements OnInit {

  @Input() user: User;

  constructor() {
  }

  ngOnInit() {
  }

}
