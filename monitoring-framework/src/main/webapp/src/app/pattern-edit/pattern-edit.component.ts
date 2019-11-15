import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { PatternService } from '../services/pattern.service';
import { FormControl, NgForm } from '@angular/forms';
import {MatSnackBar} from "@angular/material";
import { MatAutocomplete, MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { map, startWith } from 'rxjs/operators';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { TemplateService } from '../services/template.service';

@Component({
  selector: 'app-pattern-edit',
  templateUrl: './pattern-edit.component.html',
  styleUrls: ['./pattern-edit.component.css']
})
export class PatternEditComponent implements OnInit, OnDestroy {
  pattern: any = {};

  filteredTemplates: Observable<any[]>;
  allTemplates: any[] = [];
  selectedTemplates: any[] = [];
  separatorKeysCodes: number[] = [ENTER, COMMA];
  templateCtrl = new FormControl();
  sub: Subscription;

  // @ts-ignore
  @ViewChild('templateInput', {static: false}) templateInput: ElementRef<HTMLInputElement>;
  // @ts-ignore
  @ViewChild('auto', {static: false}) matAutocomplete: MatAutocomplete;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private patternService: PatternService,
    private templateService: TemplateService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.fetchListOfAllTemplates();
    this.sub = this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.patternService.get(id).subscribe((pattern: any) => {
          if (pattern) {
            this.pattern = pattern;
            this.pattern.id = id;
            this.fetchListOfLinkedTemplates(pattern.id);
          } else {
            console.log(`Pattern with id '${id}' not found, returning to list`);
            //this.gotoList();
          }
        });
      }
    });
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  private fetchListOfAllTemplates() {
    this.templateService.getAll().subscribe((templates: any[]) => {
      this.allTemplates = templates;
      this.filteredTemplates = this.templateCtrl.valueChanges.pipe(
        startWith(null),
        map((template: any | null) => template ? this._filter(template): this.allTemplates.slice())
      )
    })
  }

  private fetchListOfLinkedTemplates(patternId){
    this.patternService.getAllTemplatesOfPattern(patternId).subscribe((templates: any) => {
      this.selectedTemplates = templates;
      this.selectedTemplates.forEach( template => this._filter(template));
      this.templateCtrl.setValue(null);
    })
  }

  gotoList() {
    this.router.navigate(['/pattern-list']);
  }

  openSnackBar(message: string, action: string, duration: number) {
    this.snackBar.open(message, action, {
      duration: duration,
    });
  }

  save(form: NgForm) {
    if (form.name.trim() != "") {
      this.patternService.add(this.pattern).subscribe(response => {
        this.saveTemplatesOfPattern(response.body.id)
      }, error => {
        console.error(error);
      });
    } else {
      this.openSnackBar('Pattern name must not be empty', 'close', 2000)
    }
  }

  private saveTemplatesOfPattern(patternId){
    this.patternService.updateTemplatesOfPattern(patternId, this.selectedTemplates).subscribe(response => {
      this.gotoList();
    })
  }

  remove(form: NgForm) {
    this.patternService.remove(form).subscribe(result => {
      this.gotoList();
    }, error => console.error(error));
  }

  cancel() {
    this.gotoList();
  }

  removeTemplate(template: any[]): void {
    const index = this.selectedTemplates.indexOf(template);
    if (index >= 0) {
      this.selectedTemplates.splice(index, 1);
      this.allTemplates.push(template);
      this.templateCtrl.setValue(null);
    }
  }


  selected(event: MatAutocompleteSelectedEvent): void {
    this.selectedTemplates.push(event.option.value);
    this.templateInput.nativeElement.value = '';
    this.templateCtrl.setValue(null);
  }

  private _filter(value: any): string[] {
    const index = this.allTemplates.findIndex(template => template.id == value.id);
    return this.allTemplates.splice(index, 1);
  }


}
