import { Component, OnInit } from '@angular/core';
import { TemplateService } from '../services/template.service';
import { FileUploadService } from '../services/fileUpload.service';

@Component({
  selector: 'app-instrumentation-template-overview',
  templateUrl: './instrumentation-template-overview.component.html',
  styleUrls: ['./instrumentation-template-overview.component.css']
})
export class InstrumentationTemplateOverviewComponent implements OnInit {
  templates: Array<any>;
  language = "typescript";
  dataToDisplay;

  constructor(
    private templateService: TemplateService,
    private fileService: FileUploadService
    ) { }

  ngOnInit() {
   this.fetchAllTemplates();
  }

  remove(template) {
    this.templateService.remove(template.id).subscribe(result => {
      let index = this.templates.findIndex(templateIn => templateIn.id === template.id);
      this.fetchAllTemplates();
    }, error => console.error(error));
  }

  private fetchAllTemplates() {
    this.templateService.getAll().subscribe(data => {
      this.templates = data;
      this.fetchFilesOfTemplates()
    })
  }

  fetchFilesOfTemplates(){
    this.templates.forEach(template => {
      if(template.file) {
        let decodedString = atob(template.file.data);
        template.decodedFile = decodedString;
        console.log(decodedString);
      }
    })
  }


}
