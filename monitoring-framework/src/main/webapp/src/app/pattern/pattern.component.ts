import { Component, OnInit } from '@angular/core';
import { PatternService } from '../services/pattern.service';

@Component({
  selector: 'app-pattern',
  templateUrl: './pattern.component.html',
  styleUrls: ['./pattern.component.css']
})
export class PatternComponent implements OnInit {
  patterns: Array<any>;

  constructor(private patternService: PatternService) { }

  ngOnInit() {
    this.patternService.getAll().subscribe(data => {
      this.patterns = data;
    })
  }
}
