import { Component } from '@angular/core';
import {MdButton, MdCard, MdToolbar, MdInput} from "@angular/material";
import {RecordService} from "./record.service"

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  providers: [MdCard, MdToolbar, MdInput, MdButton, RecordService],
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app works well!';
  private name = "";
  private sex = "";
  private otherSex = "";
  private whatHappend = "";

  constructor (private recordService: RecordService){}


  clicked(event) {
    this.recordService.createRecord({name: this.name, sex: this.sex, whatHappend: this.whatHappend})
        .subscribe(data => {alert(data)},
        err => alert(err));
    alert("Name:" + this.name + ", Geschlecht " + this.getSex());
  }

  private getSex() {
    if (this.sex == "other") {
      return this.otherSex;
    } else {
      return this.sex;
    }
  }
}
