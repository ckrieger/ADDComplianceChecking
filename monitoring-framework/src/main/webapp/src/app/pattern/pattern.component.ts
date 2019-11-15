import { Component, OnInit } from '@angular/core';
import { PatternService } from '../services/pattern.service';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-pattern',
  templateUrl: './pattern.component.html',
  styleUrls: ['./pattern.component.css']
})
export class PatternComponent implements OnInit {
  patterns: Array<any>;

  constructor(
    private patternService: PatternService,
    private router: Router,
  ) { }

  ngOnInit() {
    this.fetchAllPatterns()
  }


  remove(pattern) {
    this.patternService.remove(pattern.id).subscribe(result => {
      let index = this.patterns.findIndex(patternIn => patternIn.id === pattern.id);
      this.fetchAllPatterns();
    }, error => console.error(error));
  }

  private fetchAllPatterns(){
    this.patternService.getAll().subscribe(data => {
      this.patterns = data;
      this.patterns.forEach(pattern => this.fetchListOfLinkedTemplates(pattern))
    })
  }

  private fetchListOfLinkedTemplates(pattern){
    let index = this.patterns.findIndex(patternIn => patternIn.id === pattern.id);
    this.patternService.getAllTemplatesOfPattern(pattern.id).subscribe((templates: any) => {
      this.patterns[index].templates = templates;
    })
  }
}
