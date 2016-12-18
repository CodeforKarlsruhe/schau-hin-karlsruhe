import { Component } from '@angular/core';
import {MdButton, MdCard, MdToolbar, MdInput} from "@angular/material";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  providers: [MdCard, MdToolbar, MdInput, MdButton],
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app works well!';
}
