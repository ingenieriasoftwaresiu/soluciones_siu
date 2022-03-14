/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.jobs;

import co.edu.udea.solucionessiu.exception.GIDaoException;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author jorge.correaj
 */
public class ProgramacionNotificarVencimientoPlanMntoSIU {
    
    public void crearProgramacion(){
        
        Integer intError=0;
        
        try {
            
            new GIDaoException("Inicio la programación de la tarea NotificarVencimientoPlanMntoSIU");
                                        
            intError = 1;
            
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start(); 
            
            // Creación de una instancia de JobDetail.
            JobDetail jobDetail = new JobDetail("NotificarVencimientoPlanMntoSIUJob", scheduler.DEFAULT_GROUP, NotificarVencimientoPlanMntoSIU.class);
            
            intError = 2;
            
            // Se crea el trigger para ejecución el primer día del mes a las 06:45 a.m.  
             Trigger trigger = new CronTrigger("NotificarVencimientoPlanMntoSIUTrigger", Scheduler.DEFAULT_GROUP, "00 45 06 1 * ?");
             trigger.setName("tgNotificarVencimientoPlanMntoSIU");
             trigger.setGroup("grupoSIUWEB");
             trigger.setPriority(1);
                                                  
             intError = 3;

            // Registro dentro del Scheduler.            
            scheduler.scheduleJob(jobDetail, trigger);
                        
             intError = 4;
             
             new GIDaoException("Finalizó la programación de la tarea NotificarVencimientoPlanMntoSIU.");

        } catch(Exception e) {
            new GIDaoException("Se generó un error al ejecutar la tarea NotificarVencimientoPlanMntoSIU. Variable error = " +  intError, e);
        }
    } 
    
}
